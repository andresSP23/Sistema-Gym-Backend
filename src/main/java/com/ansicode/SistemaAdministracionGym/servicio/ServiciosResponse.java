package com.ansicode.SistemaAdministracionGym.servicio;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiciosResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionDias;
    private BigDecimal precio;
    private Boolean esSuscripcion;
    private Boolean estado;
}
