package com.ansicode.SistemaAdministracionGym.detalleventa;

import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaResponse {

    private Long id;

    private TipoItemVenta tipoItem;
    private Long referenciaId;

    private String descripcionSnapshot;
    private BigDecimal precioUnitarioSnapshot;
    private BigDecimal cantidad;

    private BigDecimal descuento;
    private BigDecimal impuesto;
    private BigDecimal totalLinea;
}
