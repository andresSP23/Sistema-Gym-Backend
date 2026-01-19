package com.ansicode.SistemaAdministracionGym.producto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequest {
    @NotBlank
    private String nombre;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precioCompra;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precioVenta;

    @NotNull
    private Long categoriaProductoId;



}
