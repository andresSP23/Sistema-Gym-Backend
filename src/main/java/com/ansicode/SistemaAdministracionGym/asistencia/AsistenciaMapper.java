package com.ansicode.SistemaAdministracionGym.asistencia;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.clientesuscripcion.ClienteSuscripcion;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.EstadoSuscripcion;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AsistenciaMapper {
    public AsistenciaResponse toAsistenciaResponse(
            Asistencia asistencia,
            ClienteSuscripcion suscripcion,
            List<Pago> pagos,
            long diasRestantes
    ) {
        Cliente cliente = asistencia.getCliente();

        AsistenciaResponse response = new AsistenciaResponse();
        response.setId(asistencia.getId());
        response.setClienteId(cliente.getId());
        response.setClienteNombre(cliente.getNombres() + " " + cliente.getApellidos());
        response.setFechaEntrada(asistencia.getFechaEntrada());
        response.setDiasRestantes(diasRestantes);

        // ✅ Suscripción
        if (suscripcion != null) {
            boolean vigente = suscripcion.getEstado() == EstadoSuscripcion.ACTIVA
                    && suscripcion.getFechaFin() != null
                    && suscripcion.getFechaFin().isAfter(LocalDateTime.now());

            response.setMembresiaActiva(vigente);
            response.setMembresiaClienteId(suscripcion.getId()); // reutilizas el campo para "suscripcionId"
            response.setMembresiaNombre(suscripcion.getServicio().getNombre());
        } else {
            response.setMembresiaActiva(false);
            response.setMembresiaClienteId(null);
            response.setMembresiaNombre(null);
        }

        // Pagos pendientes (en el nuevo flujo, normalmente false si ya existe suscripción)
        response.setPagosPendientes(
                pagos != null && pagos.stream().anyMatch(p -> p.getEstado() != EstadoPago.COMPLETADO)
        );

        return response;
    }
}
