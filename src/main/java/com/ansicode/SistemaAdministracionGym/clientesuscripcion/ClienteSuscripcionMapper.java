package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import org.springframework.stereotype.Component;

@Component
public class ClienteSuscripcionMapper {

    public ClienteSuscripcionResponse toResponse(ClienteSuscripcion cs) {
        if (cs == null) return null;

        return ClienteSuscripcionResponse.builder()
                .id(cs.getId())

                // Cliente
                .clienteId(cs.getCliente() != null ? cs.getCliente().getId() : null)
                .clienteNombre(cs.getCliente() != null ? cs.getCliente().getNombreCompleto() : null)

                // Servicio
                .servicioId(cs.getServicio() != null ? cs.getServicio().getId() : null)
                .servicioNombre(cs.getServicio() != null ? cs.getServicio().getNombre() : null)

                // Venta
                .ventaId(cs.getVenta() != null ? cs.getVenta().getId() : null)

                // Fechas / estado
                .fechaInicio(cs.getFechaInicio())
                .fechaFin(cs.getFechaFin())
                .estado(cs.getEstado())

                // sin lógica aquí
                .diasRestantes(null)

                .build();
    }
}