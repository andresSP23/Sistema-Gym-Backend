package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoComprobante;
import com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {

    @NotNull(message = "ventaId es obligatorio")
    private Long ventaId;

    // opcional (si no viene, se usa el de la venta)
    private Long clienteId;

    @NotNull(message = "metodo es obligatorio")
    private MetodoPago metodo; // EFECTIVO, TRANSFERENCIA, TARJETA, etc.

    @NotNull(message = "monto es obligatorio")
    @DecimalMin(value = "0.01", message = "monto debe ser mayor a 0")
    private BigDecimal monto;

    // solo cuando metodo == EFECTIVO
    private BigDecimal efectivoRecibido;

    // opcional para transferencias/tarjeta
    private String referenciaTransaccion;

    @NotNull(message = "tipoOperacion es obligatorio")
    private TipoOperacionPago tipoOperacion; // PRODUCTO / SERVICIO / MIXTO

    @NotNull(message = "tipoComprobante es obligatorio")
    private TipoComprobante tipoComprobante; // FACTURA / RECIBO

    // opcional, por defecto USD
    private String moneda;

    // opcional para Movimientos Bancarios
    private Long bancoId;
}