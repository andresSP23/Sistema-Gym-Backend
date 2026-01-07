package com.ansicode.SistemaAdministracionGym.membresia;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembresiaRequest {
    @NotBlank(message = "El nombre de la membresía es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    @NotNull(message = "La duración en días es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer duracionDias;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    @NotNull(message = "Debe indicar si permite congelación")
    private Boolean permiteCongelacion;
}
