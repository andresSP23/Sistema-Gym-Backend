package com.ansicode.SistemaAdministracionGym.asistencia;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaResponse {

    private Long id;

    private Long clienteId;
    private String clienteNombre; // combinación de nombres + apellidos

    private LocalDateTime fechaEntrada;
}
