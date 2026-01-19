package com.ansicode.SistemaAdministracionGym.servicio;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ServiciosRequest {

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "descripcion es obligatoria")
    @Size(max = 255)
    private String descripcion;

    @NotNull(message = "duracionDias es obligatoria")
    @Min(1)
    private Integer duracionDias;

    @NotNull(message = "precio es obligatorio")
    @DecimalMin("0.01")
    private BigDecimal precio;

    @NotNull(message = "suscripcion es obligatorio")
    private Boolean esSuscripcion;

    @NotNull(message = "activo es obligatorio")
    private Boolean estado;
}
