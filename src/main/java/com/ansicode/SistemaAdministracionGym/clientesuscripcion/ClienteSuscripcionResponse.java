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
    private String clienteNombre;

    private Long servicioId;
    private String servicioNombre;

    private Long ventaId;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private EstadoSuscripcion estado;

    private Long diasRestantes;
}