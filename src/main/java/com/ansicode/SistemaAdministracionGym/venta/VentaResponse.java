package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VentaResponse {
    private Long id;

    private Long clienteId;
    private String clienteNombre;

    private Long vendedorId;
    private String vendedorNombre;

    private BigDecimal total;
    private LocalDateTime fechaVenta;

    private List<DetalleVentaResponse> detalles;

    private boolean activo;
}
