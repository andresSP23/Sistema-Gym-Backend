package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoComprobante;
import com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {

    private Long id;

    // Venta / factura
    private Long ventaId;
    private String numeroFactura;

    // Cliente
    private Long clienteId;
    private String nombreCliente;

    // Pago
    private MetodoPago metodo;
    private String moneda;
    private BigDecimal monto;
    private BigDecimal efectivoRecibido;
    private BigDecimal cambio;
    private String referenciaTransaccion;

    private TipoOperacionPago tipoOperacion;
    private TipoComprobante tipoComprobante;
    private EstadoPago estado;

    private LocalDateTime fechaPago;

    // Comprobante / PDF
    private Long comprobanteId;
    private String pdfRef;
}