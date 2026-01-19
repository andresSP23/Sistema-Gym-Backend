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

import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroService;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioService;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import com.ansicode.SistemaAdministracionGym.venta.VentaRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class    PagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final MovimientoDineroService movimientoDineroService;
    private final ComprobanteService comprobanteService;
    private final ComprobanteRepository  comprobanteRepository;
    private final PagoMapper pagoMapper;
    private final ClienteService clienteService;
    private final MovimientoInventarioService  movimientoInventarioService;
    private final ProductoRepository  productoRepository;
    private final ClienteSuscripcionService clienteSuscripcionService;


    @Transactional
    public PagoResponse registrarPago(PagoRequest request, Authentication connectedUser) {

        if (request.getVentaId() == null) {
            throw new IllegalArgumentException("ventaId es obligatorio");
        }
        if (request.getMetodo() == null) {
            throw new IllegalArgumentException("metodo es obligatorio");
        }
        if (request.getTipoComprobante() == null) {
            throw new IllegalArgumentException("tipoComprobante es obligatorio");
        }

        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));

        // Bloqueos por estado
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new IllegalArgumentException("No se puede pagar una venta ANULADA");
        }
        if (venta.getEstado() == EstadoVenta.REEMBOLSADA) {
            throw new IllegalArgumentException("No se puede pagar una venta REEMBOLSADA");
        }
        if (venta.getEstado() == EstadoVenta.CONFIRMADA) {
            throw new IllegalArgumentException("La venta ya está confirmada/pagada");
        }

        // Validar detalles
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("No se puede pagar una venta sin detalles");
        }

        boolean tieneServicio = venta.getDetalles().stream()
                .anyMatch(d -> d.getTipoItem() == TipoItemVenta.SERVICIO);

        boolean tieneProducto = venta.getDetalles().stream()
                .anyMatch(d -> d.getTipoItem() == TipoItemVenta.PRODUCTO);

        // (Opcional recomendado) si no vas a soportar MIXTO aquí, bloquéalo
        if (tieneServicio && tieneProducto) {
            throw new IllegalArgumentException("Esta venta es MIXTA. Usa el flujo mixto.");
        }

        // Resolver cliente:
        // - SERVICIO: obligatorio
        // - PRODUCTO: puede ser null
        Cliente cliente = null;

        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

            // coherencia: si venta ya tiene cliente, debe coincidir
            if (venta.getCliente() != null && !venta.getCliente().getId().equals(cliente.getId())) {
                throw new IllegalArgumentException("El cliente del pago no coincide con el cliente de la venta");
            }
        } else {
            // usar el de la venta (puede ser null y está bien si es PRODUCTO)
            cliente = venta.getCliente();
        }

        if (tieneServicio && cliente == null) {
            throw new IllegalArgumentException("Para servicios el cliente es obligatorio");
        }

        // Validar total venta
        if (venta.getTotal() == null || venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La venta no tiene un total válido");
        }

        BigDecimal totalVenta = venta.getTotal().setScale(2, RoundingMode.HALF_UP);

        // Evitar null en sum
        BigDecimal pagado = pagoRepository.sumMontoByVentaAndEstado(venta.getId(), EstadoPago.COMPLETADO);
        if (pagado == null) pagado = BigDecimal.ZERO;
        pagado = pagado.setScale(2, RoundingMode.HALF_UP);

        BigDecimal restante = totalVenta.subtract(pagado).setScale(2, RoundingMode.HALF_UP);

        if (restante.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La venta no tiene saldo pendiente");
        }

        // Validar monto
        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("monto debe ser mayor a 0");
        }

        BigDecimal monto = request.getMonto().setScale(2, RoundingMode.HALF_UP);

        // Reglas de monto:
        // - SERVICIO (Opción A): pago exacto
        // - PRODUCTO: aquí te lo dejo también exacto (si quieres permitir parcial, hay que meter estado PARCIAL)
        if (tieneServicio) {
            if (monto.compareTo(restante) != 0) {
                throw new IllegalArgumentException("Para servicios debes pagar el monto exacto pendiente: " + restante);
            }
        } else {
            // PRODUCTO: por ahora exacto (cambia esto si luego permites parcial)
            if (monto.compareTo(restante) != 0) {
                throw new IllegalArgumentException("Debes pagar el monto exacto pendiente: " + restante);
            }
        }

        // EFECTIVO
        BigDecimal cambio = null;
        BigDecimal efectivoRecibido = null;

        if (request.getMetodo() == MetodoPago.EFECTIVO) {
            if (request.getEfectivoRecibido() == null) {
                throw new IllegalArgumentException("efectivoRecibido es obligatorio para EFECTIVO");
            }
            efectivoRecibido = request.getEfectivoRecibido().setScale(2, RoundingMode.HALF_UP);

            if (efectivoRecibido.compareTo(monto) < 0) {
                throw new IllegalArgumentException("El efectivo recibido no puede ser menor al monto");
            }

            cambio = efectivoRecibido.subtract(monto).setScale(2, RoundingMode.HALF_UP);
        }

        // TipoOperacion: NO del front, se deduce
        TipoOperacionPago tipoOperacion = tieneServicio
                ? TipoOperacionPago.SERVICIO
                : TipoOperacionPago.PRODUCTO;

        // Crear Pago
        Pago pago = Pago.builder()
                .venta(venta)
                .cliente(cliente) // puede ser null si PRODUCTO mostrador
                .fechaPago(LocalDateTime.now())
                .metodo(request.getMetodo())
                .moneda(request.getMoneda() == null ? "USD" : request.getMoneda())
                .monto(monto)
                .efectivoRecibido(efectivoRecibido)
                .cambio(cambio)
                .referenciaTransaccion(request.getReferenciaTransaccion())
                .tipoOperacion(tipoOperacion)
                .tipoComprobante(request.getTipoComprobante())
                .estado(EstadoPago.COMPLETADO)
                .build();

        Pago savedPago = pagoRepository.save(pago);

        // 1) MovimientoDinero (INGRESO)
        MovimientoDineroCreateRequest movReq = new MovimientoDineroCreateRequest();
        movReq.setSucursalId(venta.getSucursal().getId());
        movReq.setTipo(TipoMovimientoDinero.INGRESO);
        movReq.setConcepto(ConceptoMovimientoDinero.PAGO_VENTA);
        movReq.setMetodo(savedPago.getMetodo());
        movReq.setMoneda(savedPago.getMoneda());
        movReq.setMonto(savedPago.getMonto());
        movReq.setDescripcion("Pago venta " + venta.getNumeroFactura());
        movimientoDineroService.crearMovimiento(movReq, connectedUser);

        // 2) Comprobante + PDF (evitar duplicado por reintento)
        // Si tienes repo para comprobantes:
        Optional<Comprobante> comprobanteOpt =
                comprobanteRepository.findTopByVentaIdAndTipoAndEstadoOrderByCreatedAtDesc(
                        venta.getId(),
                        request.getTipoComprobante(),
                        EstadoComprobante.GENERADO
                );

        Comprobante comprobante;
        if (comprobanteOpt.isPresent() && comprobanteOpt.get().getPdfRef() != null) {
            comprobante = comprobanteOpt.get();
        } else {
            // tu service debe crear Comprobante y setear pdfRef
            comprobante = comprobanteService.generarFacturaPdf(venta);
        }

        savedPago.setComprobante(comprobante);
        savedPago.setTipoComprobante(comprobante.getTipo());
        savedPago = pagoRepository.save(savedPago);



        if (tieneProducto) {
            for (DetalleVenta d : venta.getDetalles()) {

                if (d.getTipoItem() != TipoItemVenta.PRODUCTO) continue;

                if (d.getCantidad() == null) {
                    throw new IllegalArgumentException("Detalle de producto sin cantidad");
                }

                final int unidades;
                try {
                    unidades = d.getCantidad().intValueExact();
                } catch (ArithmeticException ex) {
                    throw new IllegalArgumentException("La cantidad de producto debe ser entera. Valor: " + d.getCantidad());
                }

                if (unidades <= 0) {
                    throw new IllegalArgumentException("Cantidad de producto inválida: " + unidades);
                }

                Producto producto = productoRepository.findById(d.getReferenciaId())
                        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + d.getReferenciaId()));

                movimientoInventarioService.registrarSalida(producto, unidades, "Salida por venta " + venta.getNumeroFactura());

                // Por si tu registrar() no guarda producto (en tu código solo hace producto.setStock y repository.save(m))
                // esto asegura persistencia del stock nuevo:
                productoRepository.save(producto);
            }
        }


        // 3) Confirmar venta
        venta.setEstado(EstadoVenta.CONFIRMADA);
        ventaRepository.save(venta);

        // 4) Activar cliente SOLO si hay servicios
//        if (tieneServicio) {
//            clienteService.activarCliente(cliente.getId());
//        }

        if (tieneServicio) {
            clienteSuscripcionService.registrarSuscripcionDesdeVenta(venta, savedPago.getFechaPago());
        }



        return pagoMapper.toResponse(savedPago);
    }



}
