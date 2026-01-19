package com.ansicode.SistemaAdministracionGym.venta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ItemProductoRequest {
    @NotNull
    private Long productoId;

    @NotNull
    @DecimalMin("0.001")
    private BigDecimal cantidad; // s
}
