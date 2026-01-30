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

    @Transactional
    public void registrarSuscripcionDesdeVenta(Venta venta, LocalDateTime fechaPago) {

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
                        BusinessErrorCodes.SUSCRIPCION_VENTA_SIN_DETALLE_SERVICIO
                ));

        Servicios servicio = serviciosRepository.findById(detServicio.getReferenciaId())
                .orElseThrow(() -> new BussinessException(
                        BusinessErrorCodes.SUSCRIPCION_SERVICIO_NOT_FOUND
                ));

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

        Optional<ClienteSuscripcion> ultimaActivaOpt =
                clienteSuscripcionRepository.findTopByClienteIdAndEstadoAndFechaFinAfterOrderByFechaFinDesc(
                        cliente.getId(),
                        EstadoSuscripcion.ACTIVA,
                        LocalDateTime.now()
                );

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
            ClienteSuscripcionMapper mapper
    ) {
        Specification<ClienteSuscripcion> spec = Specification.allOf(
                ClienteSuscripcionSpecifications.clienteVisible(),
                ClienteSuscripcionSpecifications.clienteId(clienteId),
                ClienteSuscripcionSpecifications.servicioId(servicioId),
                ClienteSuscripcionSpecifications.estado(estado),
                ClienteSuscripcionSpecifications.vigente(vigente),
                ClienteSuscripcionSpecifications.fechaInicioDesde(desde),
                ClienteSuscripcionSpecifications.fechaInicioHasta(hasta)
        );

        Page<ClienteSuscripcion> page = clienteSuscripcionRepository.findAll(spec, pageable);

        return PageResponse.<ClienteSuscripcionResponse>builder()
                .content(
                        page.getContent().stream()
                                .map(cs -> {
                                    ClienteSuscripcionResponse res = mapper.toResponse(cs);
                                    res.setDiasRestantes(calcularDiasRestantes(cs.getFechaFin()));
                                    return res;
                                })
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

    // =========================
    // HELPERS
    // =========================
    private Long calcularDiasRestantes(LocalDateTime fechaFin) {
        if (fechaFin == null) return 0L;

        long dias = ChronoUnit.DAYS.between(LocalDateTime.now(), fechaFin);
        return Math.max(dias, 0);
    }
}
