package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VentaResponse {

    private Integer id;

    private Integer clienteId;
    private String clienteNombre; // nombres + apellidos

    private Integer vendedorId;
    private String vendedorNombre; // nombres + apellidos

    private BigDecimal total;
    private EstadoVenta estadoVenta;

    private LocalDateTime fechaVenta;

    private boolean activo;
}
