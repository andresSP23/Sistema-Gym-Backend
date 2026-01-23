package com.ansicode.SistemaAdministracionGym.conteocaja;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConteoCajaItemResponse {

    private Long id;
    private String moneda;

    private BigDecimal denominacion;
    private Integer cantidad;
    private BigDecimal subtotal;
}
