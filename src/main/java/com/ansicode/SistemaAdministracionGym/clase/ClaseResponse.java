package com.ansicode.SistemaAdministracionGym.clase;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaseResponse {
    private Long id;

    private String nombre;

    private Long entrenadorId;
    private String entrenadorNombre;
}
