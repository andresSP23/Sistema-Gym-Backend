package com.ansicode.SistemaAdministracionGym.comprobanteventa;

import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.venta.DetalleVentaResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ComprobanteVentaResponse {

    private Long id;
    private Long ventaId;
    private Long clienteId;
    private String clienteNombre;
    private Long vendedorId;
    private String vendedorNombre;
    private MetodoPago metodoPago;
    private BigDecimal total;
    private LocalDateTime fechaVenta;
    private     List<DetalleVentaResponse> detalles;
    private String sucursalNombre;
    private String sucursalDireccion;
    private LocalDateTime fechaGeneracion;
}
