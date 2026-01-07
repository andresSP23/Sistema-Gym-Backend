package com.ansicode.SistemaAdministracionGym.membresia;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembresiaResponse {

    private Long id;
    private String nombre;
    private Integer duracionDias;
    private BigDecimal precio;
    private String descripcion;
    private Boolean permiteCongelacion;
    private boolean activo;

}
