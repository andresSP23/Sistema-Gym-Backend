package com.ansicode.SistemaAdministracionGym.producto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AjustarStockRequest {
    @NotNull(message = "El stock real es obligatorio")
    @Min(value = 0, message = "El stock real no puede ser negativo")
    private Integer stockReal;

    @Size(max = 250, message = "La observación no puede exceder 250 caracteres")
    private String observacion;
}
