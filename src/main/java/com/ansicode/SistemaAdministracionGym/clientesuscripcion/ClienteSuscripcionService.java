package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import com.ansicode.SistemaAdministracionGym.enums.EstadoSuscripcion;
import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.servicio.Servicios;
import com.ansicode.SistemaAdministracionGym.servicio.ServiciosRepository;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteSuscripcionService {

    private final ClienteSuscripcionRepository clienteSuscripcionRepository;
    private final ServiciosRepository serviciosRepository;
    private final com.ansicode.SistemaAdministracionGym.venta.VentaService ventaService;
    private final com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroService movimientoDineroService;

    @Transactional
    public void registrarSuscripcionDesdeVenta(Venta venta, LocalDateTime fechaPago) {
        // ... (el código de este método no cambia, pero necesitamos reinyeccionar las
        // dependencias arriba)
        if (venta == null) {
            throw new BussinessException(BusinessErrorCodes.SUSCRIPCION_VENTA_REQUIRED);
        }

        Cliente cliente = venta.getCliente();
        if (cliente == null) {
            throw new BussinessException(BusinessErrorCodes.SUSCRIPCION_CLIENTE_REQUIRED);
        }

        // Anti-duplicado por reintento
        if (clienteSuscripcionRepository.existsByVentaId(venta.getId())) {
            return;
        }

        DetalleVenta detServicio = venta.getDetalles().stream()
                .filter(d -> d.getTipoItem() == TipoItemVenta.SERVICIO)
                .findFirst()
                .orElseThrow(() -> new BussinessException(
                        BusinessErrorCodes.SUSCRIPCION_VENTA_SIN_DETALLE_SERVICIO));

        Servicios servicio = serviciosRepository.findById(detServicio.getReferenciaId())
                .orElseThrow(() -> new BussinessException(
                        BusinessErrorCodes.SUSCRIPCION_SERVICIO_NOT_FOUND));

        // Si no es suscripción, aquí NO hacemos nada
        if (!servicio.isEsSuscripcion()) {
            return;
        }

        if (servicio.getDuracionDias() == null || servicio.getDuracionDias() < 1) {
            throw new BussinessException(BusinessErrorCodes.SUSCRIPCION_DURACION_INVALIDA);
        }

        LocalDateTime inicioPago = (fechaPago != null) ? fechaPago : LocalDateTime.now();

        // Renovación inteligente
        LocalDateTime base = inicioPago;

        Optional<ClienteSuscripcion> ultimaActivaOpt = clienteSuscripcionRepository
                .findTopByClienteIdAndEstadoAndFechaFinAfterOrderByFechaFinDesc(
                        cliente.getId(),
                        EstadoSuscripcion.ACTIVA,
                        LocalDateTime.now());

        if (ultimaActivaOpt.isPresent()) {
            LocalDateTime finActual = ultimaActivaOpt.get().getFechaFin();
            if (finActual != null && finActual.isAfter(inicioPago)) {
                base = finActual;
            }
        }

        LocalDateTime fechaFin = base.plusDays(servicio.getDuracionDias());

        ClienteSuscripcion cs = ClienteSuscripcion.builder()
                .cliente(cliente)
                .servicio(servicio)
                .venta(venta)
                .fechaInicio(inicioPago)
                .fechaFin(fechaFin)
                .estado(EstadoSuscripcion.ACTIVA)
                .build();

        clienteSuscripcionRepository.save(cs);
    }

    @Transactional(readOnly = true)
    public ClienteSuscripcionResponse obtenerSuscripcionActiva(Long clienteId, ClienteSuscripcionMapper mapper) {
        List<ClienteSuscripcion> list = clienteSuscripcionRepository.findActivaVigente(clienteId, LocalDateTime.now());
        return list.isEmpty() ? null : mapper.toResponse(list.get(0));
    }

    @Transactional(readOnly = true)
    public PageResponse<ClienteSuscripcionResponse> listarConFiltros(
            Long clienteId,
            Long servicioId,
            String estado,
            Boolean vigente,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable,
            ClienteSuscripcionMapper mapper) {
        Specification<ClienteSuscripcion> spec = Specification.allOf(
                ClienteSuscripcionSpecifications.clienteVisible(),
                ClienteSuscripcionSpecifications.clienteId(clienteId),
                ClienteSuscripcionSpecifications.servicioId(servicioId),
                ClienteSuscripcionSpecifications.estado(estado),
                ClienteSuscripcionSpecifications.vigente(vigente),
                ClienteSuscripcionSpecifications.fechaInicioDesde(desde),
                ClienteSuscripcionSpecifications.fechaInicioHasta(hasta));

        Page<ClienteSuscripcion> page = clienteSuscripcionRepository.findAll(spec, pageable);

        return PageResponse.<ClienteSuscripcionResponse>builder()
                .content(
                        page.getContent().stream()
                                .map(cs -> {
                                    ClienteSuscripcionResponse res = mapper.toResponse(cs);
                                    res.setDiasRestantes(calcularDiasRestantes(cs.getFechaFin()));
                                    return res;
                                })
                                .toList())
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ... (otros métodos)

    @Transactional
    public void cancelarSuscripcion(Long id, CancelarSuscripcionRequest request,
            org.springframework.security.core.Authentication connectedUser) {
        ClienteSuscripcion cs = clienteSuscripcionRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SUSCRIPCION_NOT_FOUND));

        if (cs.getEstado() == EstadoSuscripcion.CANCELADA) {
            throw new BussinessException(BusinessErrorCodes.SUSCRIPCION_YA_CANCELADA);
        }

        cs.setEstado(EstadoSuscripcion.CANCELADA);

        // Logica de Devolución
        if (request != null && Boolean.TRUE.equals(request.getDevolverDinero())) {
            if (request.getMontoDevolucion() == null
                    || request.getMontoDevolucion().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_MONTO_INVALIDO);
            }
            if (request.getMetodoDevolucion() == null) {
                throw new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_METODO_REQUIRED);
            }

            com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest egreso = new com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest();

            egreso.setMonto(request.getMontoDevolucion());
            egreso.setMetodo(request.getMetodoDevolucion());
            egreso.setTipo(com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero.EGRESO);
            egreso.setConcepto(com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero.DEVOLUCION_VENTA);
            egreso.setDescripcion("Devolución por cancelación de suscripción #" + cs.getId());
            egreso.setSucursalId(cs.getVenta().getSucursal().getId());
            egreso.setVentaId(cs.getVenta().getId());
            egreso.setServicioId(cs.getServicio().getId());
            egreso.setMoneda("USD"); // O la de la venta si se guardara

            movimientoDineroService.crearMovimiento(egreso, connectedUser);
        }

        clienteSuscripcionRepository.save(cs);
    }

    @Transactional
    public void editarSuscripcion(Long id, EditarSuscripcionRequest request) {
        ClienteSuscripcion cs = clienteSuscripcionRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SUSCRIPCION_NOT_FOUND));

        if (request.getFechaInicio() != null) {
            cs.setFechaInicio(request.getFechaInicio());
        }
        if (request.getFechaFin() != null) {
            cs.setFechaFin(request.getFechaFin());
        }

        // Validación básica de coherencia
        if (cs.getFechaInicio().isAfter(cs.getFechaFin())) {
            throw new BussinessException(BusinessErrorCodes.SUSCRIPCION_DURACION_INVALIDA);
        }

        clienteSuscripcionRepository.save(cs);
    }

    @Transactional
    public com.ansicode.SistemaAdministracionGym.venta.VentaResponse renovarSuscripcion(Long id,
            org.springframework.security.core.Authentication connectedUser) {
        ClienteSuscripcion cs = clienteSuscripcionRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SUSCRIPCION_NOT_FOUND));

        Servicios servicio = cs.getServicio();
        Cliente cliente = cs.getCliente();

        com.ansicode.SistemaAdministracionGym.venta.CrearVentaServicioRequest ventaRequest = new com.ansicode.SistemaAdministracionGym.venta.CrearVentaServicioRequest();
        ventaRequest.setSucursalId(cs.getVenta().getSucursal().getId());
        ventaRequest.setClienteId(cliente.getId());
        ventaRequest.setServicioId(servicio.getId());
        ventaRequest.setCantidad(java.math.BigDecimal.ONE);

        return ventaService.crearVentaServicio(ventaRequest, connectedUser);
    }

    // =========================
    // HELPERS
    // =========================
    private Long calcularDiasRestantes(LocalDateTime fechaFin) {
        if (fechaFin == null)
            return 0L;

        long dias = ChronoUnit.DAYS.between(LocalDateTime.now(), fechaFin);
        return Math.max(dias, 0);
    }
}
