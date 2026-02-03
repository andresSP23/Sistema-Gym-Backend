package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MovimientoBancoResponse {
    private Long id;
    private TipoMovimientoBanco tipo;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String descripcion;
    private String referencia;
}
