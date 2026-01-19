package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSuscripcion;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
public class ClienteSuscripcionResponse {
    private Long id;

    private Long clienteId;
    private Long servicioId;
    private Long ventaId;

    private String servicioNombre;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private EstadoSuscripcion estado;
}
