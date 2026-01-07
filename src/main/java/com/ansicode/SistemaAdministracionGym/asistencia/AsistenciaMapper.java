package com.ansicode.SistemaAdministracionGym.asistencia;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import org.springframework.stereotype.Service;

@Service
public class AsistenciaMapper {

    public Asistencia toAsistencia(AsistenciaRequest request, Cliente cliente) {
        return Asistencia.builder()
                .cliente(cliente)
                .fechaEntrada(request.getFechaEntrada())
                .build();
    }

    // Entidad → Response
    public AsistenciaResponse toAsistenciaResponse(Asistencia asistencia) {
        return AsistenciaResponse.builder()
                .id(asistencia.getId())
                .clienteId(asistencia.getCliente().getId())
                .clienteNombre(
                        asistencia.getCliente().getNombres() + " " +
                                asistencia.getCliente().getApellidos()
                )
                .fechaEntrada(asistencia.getFechaEntrada())
                .build();
    }
}
