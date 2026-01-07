package com.ansicode.SistemaAdministracionGym.venta;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaResponse {


    private Long id;

    private Long ventaId;
    private BigDecimal ventaTotal; // opcional para mostrar info de la venta

    private Long productoId;
    private String productoNombre; // nombre del producto

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    private boolean activo;
}
