package com.ansicode.SistemaAdministracionGym.asistencia;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.membresiacliente.MembresiaCliente;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AsistenciaMapper {
    public Asistencia toAsistencia(AsistenciaRequest request, Cliente cliente) {
        return Asistencia.builder()
                .cliente(cliente)
                .fechaEntrada(request.getFechaEntrada())
                .build();
    }

    public AsistenciaResponse toAsistenciaResponse(
            Asistencia asistencia,
            MembresiaCliente membresiaCliente,
            List<Pago> pagos ,
            long diasRestantes

    ) {
        Cliente cliente = asistencia.getCliente();

        AsistenciaResponse response = new AsistenciaResponse();
        response.setId(asistencia.getId());
        response.setClienteId(cliente.getId());
        response.setClienteNombre(cliente.getNombres() + " " + cliente.getApellidos());
        response.setFechaEntrada(asistencia.getFechaEntrada());
        response.setDiasRestantes(diasRestantes);


        // Membresía
        if (membresiaCliente != null) {
            response.setMembresiaActiva(
                    membresiaCliente.getEstado() == EstadoMembresia.ACTIVA &&
                            membresiaCliente.getFechaFin().isAfter(LocalDate.now())
            );
            response.setMembresiaClienteId(membresiaCliente.getId());
            response.setMembresiaNombre(membresiaCliente.getMembresia().getNombre());
        } else {
            response.setMembresiaActiva(false);
        }

        // Pagos pendientes
        response.setPagosPendientes(
                pagos != null &&
                        pagos.stream().anyMatch(p -> p.getEstadoPago() != EstadoPago.PAGADO)
        );






        return response;
    }
}
