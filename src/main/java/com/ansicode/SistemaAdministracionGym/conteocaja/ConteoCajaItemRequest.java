package com.ansicode.SistemaAdministracionGym.conteocaja;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConteoCajaItemRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "denominacion debe ser mayor a 0")
    private BigDecimal denominacion;

    @NotNull
    @Min(value = 0, message = "cantidad no puede ser negativa")
    private Integer cantidad;

    private String moneda = "USD";
}
