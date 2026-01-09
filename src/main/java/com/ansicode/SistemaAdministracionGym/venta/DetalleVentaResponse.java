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

    private Long productoId;
    private String productoNombre;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
