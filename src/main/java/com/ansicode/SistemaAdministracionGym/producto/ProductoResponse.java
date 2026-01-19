package com.ansicode.SistemaAdministracionGym.producto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponse {


    private Long id;
    private String nombre;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal ganancia;
    private Integer stock;

    private Long categoriaProductoId;
    private String categoriaProductoNombre;

    private boolean activo;
}
