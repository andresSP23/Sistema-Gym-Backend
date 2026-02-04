package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para crear movimientos bancarios manuales.
 */
@Data
public class MovimientoBancoRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimientoBanco tipo;

    @NotNull(message = "El concepto es obligatorio")
    private ConceptoMovimientoBanco concepto;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    private LocalDateTime fecha; // Si es null, se usa now()

    private String descripcion;

    private String referencia;

    // Origen se fija automáticamente a MANUAL para movimientos creados por este
    // endpoint
}
