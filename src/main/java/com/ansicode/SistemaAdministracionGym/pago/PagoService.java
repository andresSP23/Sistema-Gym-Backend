package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteService;
import com.ansicode.SistemaAdministracionGym.clientesuscripcion.ClienteSuscripcionService;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.comprobante.Comprobante;
import com.ansicode.SistemaAdministracionGym.comprobante.ComprobanteRepository;
import com.ansicode.SistemaAdministracionGym.comprobante.ComprobanteService;
import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import com.ansicode.SistemaAdministracionGym.enums.*;

import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroService;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioService;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import com.ansicode.SistemaAdministracionGym.venta.VentaRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;



@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final MovimientoDineroService movimientoDineroService;
    private final ComprobanteService comprobanteService;
    private final ComprobanteRepository comprobanteRepository;
    private final PagoMapper pagoMapper;
    private final ClienteService clienteService; // (no lo uso aquí, lo dejo por si lo usas luego)
    private final MovimientoInventarioService movimientoInventarioService;
    private final ProductoRepository productoRepository;
    private final ClienteSuscripcionService clienteSuscripcionService;

    @Transactional
    public PagoResponse registrarPago(PagoRequest request, Authentication connectedUser) {

        // =========================
        // VALIDACIONES BÁSICAS
        // =========================
        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getVentaId() == null) {
            throw new BussinessException(BusinessErrorCodes.PAGO_VENTA_ID_REQUIRED);
        }
        if (request.getMetodo() == null) {
            throw new BussinessException(BusinessErrorCodes.PAGO_METODO_REQUIRED);
        }
        if (request.getTipoComprobante() == null) {
            throw new BussinessException(BusinessErrorCodes.PAGO_TIPO_COMPROBANTE_REQUIRED);
        }
        if (connectedUser == null || connectedUser.getPrincipal() == null) {
            throw new BussinessException(BusinessErrorCodes.BAD_CREDENTIALS);
        }

        // =========================
        // OBTENER VENTA
        // =========================
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PAGO_VENTA_NOT_FOUND));

        // Bloqueos por estado
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new BussinessException(BusinessErrorCodes.PAGO_VENTA_ANULADA);
        }
        if (venta.getEstado() == EstadoVenta.REEMBOLSADA) {
            throw new BussinessException(BusinessErrorCodes.PAGO_VENTA_REEMBOLSADA);
        }
        if (venta.getEstado() == EstadoVenta.CONFIRMADA) {
            throw new BussinessException(BusinessErrorCodes.PAGO_VENTA_YA_PAGADA);
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new BussinessException(BusinessErrorCodes.PAGO_VENTA_SIN_DETALLES);
        }

        boolean tieneServicio = venta.getDetalles().stream()
                .anyMatch(d -> d.getTipoItem() == TipoItemVenta.SERVICIO);

        boolean tieneProducto = venta.getDetalles().stream()
                .anyMatch(d -> d.getTipoItem() == TipoItemVenta.PRODUCTO);

        if (tieneServicio && tieneProducto) {
            throw new BussinessException(BusinessErrorCodes.PAGO_VENTA_MIXTA_NO_SOPORTADA);
        }

        // =========================
        // RESOLVER CLIENTE
        // =========================
        Cliente cliente;

        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PAGO_CLIENTE_NOT_FOUND));

            if (venta.getCliente() != null && !venta.getCliente().getId().equals(cliente.getId())) {
                throw new BussinessException(BusinessErrorCodes.PAGO_CLIENTE_NO_COINCIDE_CON_VENTA);
            }
        } else {
            cliente = venta.getCliente(); // puede ser null si es PRODUCTO mostrador
        }

        if (tieneServicio && cliente == null) {
            throw new BussinessException(BusinessErrorCodes.PAGO_CLIENTE_REQUIRED_PARA_SERVICIOS);
        }

        // =========================
        // VALIDAR TOTAL / SALDO
        // =========================
        if (venta.getTotal() == null || venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.PAGO_VENTA_TOTAL_INVALIDO);
        }

        BigDecimal totalVenta = venta.getTotal().setScale(2, RoundingMode.HALF_UP);

        BigDecimal pagado = pagoRepository.sumMontoByVentaAndEstado(venta.getId(), EstadoPago.COMPLETADO);
        pagado = (pagado == null ? BigDecimal.ZERO : pagado).setScale(2, RoundingMode.HALF_UP);

        BigDecimal restante = totalVenta.subtract(pagado).setScale(2, RoundingMode.HALF_UP);

        if (restante.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.PAGO_VENTA_SIN_SALDO_PENDIENTE);
        }

        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.PAGO_MONTO_INVALIDO);
        }

        BigDecimal monto = request.getMonto().setScale(2, RoundingMode.HALF_UP);

        // Por ahora: pago exacto
        if (monto.compareTo(restante) != 0) {
            throw new BussinessException(BusinessErrorCodes.PAGO_MONTO_DEBE_SER_EXACTO);
        }

        // =========================
        // EFECTIVO
        // =========================
        BigDecimal cambio = null;
        BigDecimal efectivoRecibido = null;

        if (request.getMetodo() == MetodoPago.EFECTIVO) {
            if (request.getEfectivoRecibido() == null) {
                throw new BussinessException(BusinessErrorCodes.PAGO_EFECTIVO_RECIBIDO_REQUIRED);
            }
            efectivoRecibido = request.getEfectivoRecibido().setScale(2, RoundingMode.HALF_UP);

            if (efectivoRecibido.compareTo(monto) < 0) {
                throw new BussinessException(BusinessErrorCodes.PAGO_EFECTIVO_INSUFICIENTE);
            }

            cambio = efectivoRecibido.subtract(monto).setScale(2, RoundingMode.HALF_UP);
        }

        // TipoOperacion: NO del front, se deduce
        TipoOperacionPago tipoOperacion = tieneServicio
                ? TipoOperacionPago.SERVICIO
                : TipoOperacionPago.PRODUCTO;

        // =========================
        // CREAR PAGO
        // =========================
        Pago pago = Pago.builder()
                .venta(venta)
                .cliente(cliente)
                .clienteIdSnapshot(cliente != null ? cliente.getId() : null)
                .nombreClienteSnapshot(cliente != null
                        ? (cliente.getNombres() + " " + cliente.getApellidos())
                        : null)
                .fechaPago(LocalDateTime.now())
                .metodo(request.getMetodo())
                .moneda(normalizeMoneda(request.getMoneda()))
                .monto(monto)
                .efectivoRecibido(efectivoRecibido)
                .cambio(cambio)
                .referenciaTransaccion(request.getReferenciaTransaccion())
                .tipoOperacion(tipoOperacion)
                .tipoComprobante(request.getTipoComprobante())
                .estado(EstadoPago.COMPLETADO)
                .build();

        Pago savedPago = pagoRepository.save(pago);

        // =========================
        // 1) MOVIMIENTO DINERO (INGRESO)
        // =========================
        if (venta.getSucursal() == null || venta.getSucursal().getId() == null) {
            throw new BussinessException(BusinessErrorCodes.PAGO_SUCURSAL_REQUIRED);
        }

        MovimientoDineroCreateRequest movReq = new MovimientoDineroCreateRequest();
        movReq.setSucursalId(venta.getSucursal().getId());
        movReq.setTipo(TipoMovimientoDinero.INGRESO);
        movReq.setConcepto(ConceptoMovimientoDinero.PAGO_VENTA);
        movReq.setMetodo(savedPago.getMetodo());
        movReq.setMoneda(savedPago.getMoneda());
        movReq.setMonto(savedPago.getMonto());
        movReq.setDescripcion("Pago venta " + venta.getNumeroFactura());
        movReq.setVentaId(venta.getId());
        movReq.setPagoId(savedPago.getId());

        if (tieneServicio) {
            Long servicioId = venta.getDetalles().stream()
                    .filter(d -> d.getTipoItem() == TipoItemVenta.SERVICIO)
                    .map(DetalleVenta::getReferenciaId)
                    .findFirst()
                    .orElse(null);
            movReq.setServicioId(servicioId);
        }

        movimientoDineroService.crearMovimiento(movReq, connectedUser);

        // =========================
        // 2) COMPROBANTE + PDF
        // =========================
        Optional<Comprobante> comprobanteOpt =
                comprobanteRepository.findTopByVentaIdAndTipoAndEstadoOrderByCreatedAtDesc(
                        venta.getId(),
                        request.getTipoComprobante(),
                        EstadoComprobante.GENERADO
                );

        Comprobante comprobante;
        if (comprobanteOpt.isPresent() && comprobanteOpt.get().getPdfRef() != null && !comprobanteOpt.get().getPdfRef().isBlank()) {
            comprobante = comprobanteOpt.get();
        } else {
            comprobante = comprobanteService.generarFacturaPdf(venta);
        }

        savedPago.setComprobante(comprobante);
        savedPago.setTipoComprobante(comprobante.getTipo());
        savedPago = pagoRepository.save(savedPago);

        // =========================
        // 3) INVENTARIO (si producto)
        // =========================
        if (tieneProducto) {
            for (DetalleVenta d : venta.getDetalles()) {

                if (d.getTipoItem() != TipoItemVenta.PRODUCTO) continue;

                if (d.getCantidad() == null) {
                    throw new BussinessException(BusinessErrorCodes.PAGO_DETALLE_PRODUCTO_SIN_CANTIDAD);
                }

                final int unidades;
                try {
                    unidades = d.getCantidad().intValueExact();
                } catch (ArithmeticException ex) {
                    throw new BussinessException(BusinessErrorCodes.PAGO_DETALLE_PRODUCTO_CANTIDAD_NO_ENTERA);
                }

                if (unidades <= 0) {
                    throw new BussinessException(BusinessErrorCodes.PAGO_DETALLE_PRODUCTO_CANTIDAD_INVALIDA);
                }

                Producto producto = productoRepository.findById(d.getReferenciaId())
                        .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PAGO_PRODUCTO_NOT_FOUND));

                movimientoInventarioService.registrarSalida(
                        producto,
                        unidades,
                        "Salida por venta " + venta.getNumeroFactura()
                );

                // asegura persistencia de stock
                productoRepository.save(producto);
            }
        }

        // =========================
        // 4) CONFIRMAR VENTA
        // =========================
        venta.setEstado(EstadoVenta.CONFIRMADA);
        ventaRepository.save(venta);

        // =========================
        // 5) SUSCRIPCIÓN (si servicio)
        // =========================
        if (tieneServicio) {
            clienteSuscripcionService.registrarSuscripcionDesdeVenta(venta, savedPago.getFechaPago());
        }

        return pagoMapper.toResponse(savedPago);
    }

    @Transactional(readOnly = true)
    public PageResponse<PagoResponse> findAll(
            LocalDateTime desde,
            LocalDateTime hasta,
            TipoOperacionPago tipoOperacion,
            MetodoPago metodo,
            Pageable pageable
    ) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BussinessException(BusinessErrorCodes.PAGO_RANGO_FECHAS_INVALIDO);
        }

        Page<Pago> page = pagoRepository.buscarPagos(
                desde,
                hasta,
                tipoOperacion,
                metodo,
                pageable
        );

        return PageResponse.<PagoResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(pagoMapper::toResponse)
                                .toList()
                )
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private String normalizeMoneda(String moneda) {
        return (moneda == null || moneda.isBlank()) ? "USD" : moneda.trim().toUpperCase();
    }
}