package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EditarSuscripcionRequest {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
