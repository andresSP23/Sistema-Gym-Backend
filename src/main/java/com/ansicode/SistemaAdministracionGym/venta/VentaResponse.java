package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVentaResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
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
    private String numeroFactura;
    private LocalDateTime fechaVenta;

    private Long clienteId;
    private Long sucursalId;
    private Long cajeroUsuarioId;

    private EstadoVenta estado;

    private BigDecimal subtotal;
    private BigDecimal descuentoTotal;
    private BigDecimal impuestoTotal;
    private BigDecimal total;

    private List<DetalleVentaResponse> detalles;
}
