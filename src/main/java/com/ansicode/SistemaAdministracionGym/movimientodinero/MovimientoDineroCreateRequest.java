package com.ansicode.SistemaAdministracionGym.movimientodinero;

import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MovimientoDineroCreateRequest {

    @NotNull(message = "tipo es obligatorio")
    private TipoMovimientoDinero tipo; // INGRESO/EGRESO

    @NotNull(message = "concepto es obligatorio")
    private ConceptoMovimientoDinero concepto;

    @NotNull(message = "metodo es obligatorio")
    private MetodoPago metodo; // EFECTIVO/TARJETA/TRANSFERENCIA

    @NotNull(message = "monto es obligatorio")
    @DecimalMin(value = "0.01", message = "monto debe ser mayor a 0")
    private BigDecimal monto;

    @Size(max = 10, message = "moneda no puede exceder 10 caracteres")
    private String moneda = "USD";

    @Size(max = 250, message = "descripcion no puede exceder 250 caracteres")
    private String descripcion;
    @NotNull(message = "sucursalId es obligatorio")
    private Long sucursalId;

    // opcionales para trazabilidad
    private Long ventaId;
    private Long pagoId;
    private Long servicioId;
    private Long productoId;
}
