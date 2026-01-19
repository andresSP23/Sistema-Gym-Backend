package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import com.ansicode.SistemaAdministracionGym.enums.EstadoSuscripcion;
import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import com.ansicode.SistemaAdministracionGym.servicio.Servicios;
import com.ansicode.SistemaAdministracionGym.servicio.ServiciosRepository;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteSuscripcionService {

    private final ClienteSuscripcionRepository clienteSuscripcionRepository;
    private final ServiciosRepository serviciosRepository;

    @Transactional
    public void registrarSuscripcionDesdeVenta(Venta venta, LocalDateTime fechaPago) {

        if (venta == null) throw new IllegalArgumentException("Venta requerida");

        Cliente cliente = venta.getCliente();
        if (cliente == null) {
            throw new IllegalArgumentException("Para suscripciones el cliente es obligatorio");
        }

        // Anti-duplicado por reintento
        if (clienteSuscripcionRepository.existsByVentaId(venta.getId())) {
            return;
        }

        DetalleVenta detServicio = venta.getDetalles().stream()
                .filter(d -> d.getTipoItem() == TipoItemVenta.SERVICIO)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Venta sin detalle de servicio"));

        Servicios servicio = serviciosRepository.findById(detServicio.getReferenciaId())
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        // Si no es suscripción, aquí NO hacemos nada (o puedes marcar cliente ACTIVO si quieres)
        if (!servicio.isEsSuscripcion()) {
            return;
        }

        if (servicio.getDuracionDias() == null || servicio.getDuracionDias() < 1) {
            throw new IllegalStateException("Servicio de suscripción debe tener duracionDias válida");
        }

        LocalDateTime inicioPago = (fechaPago != null) ? fechaPago : LocalDateTime.now();

        // Renovación inteligente: si aún tiene una ACTIVA vigente, sumar desde su fechaFin
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
}
