package com.ansicode.SistemaAdministracionGym.clase;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaseResponse {
    private Integer id;

    private String nombre;

    private Integer entrenadorId;
    private String entrenadorNombre;
}
