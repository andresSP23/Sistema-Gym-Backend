package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.enums.TipoProducto;
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
    private BigDecimal precio;
    private Integer stock;
    private TipoProducto tipoProducto;
    private boolean activo;
}
