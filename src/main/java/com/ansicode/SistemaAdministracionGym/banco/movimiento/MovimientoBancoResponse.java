package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.enums.OrigenMovimientoBanco;
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
    private ConceptoMovimientoBanco concepto;
    private OrigenMovimientoBanco origen;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String descripcion;
    private String referencia;
    private Long bancoId;
    private String bancoNombre;
}
