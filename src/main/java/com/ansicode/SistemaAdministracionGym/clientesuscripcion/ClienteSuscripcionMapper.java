package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import org.springframework.stereotype.Component;

@Component
public class ClienteSuscripcionMapper {


    public ClienteSuscripcionResponse toResponse(ClienteSuscripcion cs) {
        if (cs == null) return null;

        return ClienteSuscripcionResponse.builder()
                .id(cs.getId())
                .clienteId(cs.getCliente() != null ? cs.getCliente().getId() : null)
                .servicioId(cs.getServicio() != null ? cs.getServicio().getId() : null)
                .ventaId(cs.getVenta() != null ? cs.getVenta().getId() : null)
                .servicioNombre(cs.getServicio() != null ? cs.getServicio().getNombre() : null)
                .fechaInicio(cs.getFechaInicio())
                .fechaFin(cs.getFechaFin())
                .estado(cs.getEstado())
                .build();
    }
}
