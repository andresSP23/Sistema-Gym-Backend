package com.ansicode.SistemaAdministracionGym.movimientodinero;

import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class MovimientoDineroResponse {

    private Long id;

    private Long sesionCajaId;

    private TipoMovimientoDinero tipo;
    private ConceptoMovimientoDinero concepto;
    private MetodoPago metodo;

    private String moneda;
    private BigDecimal monto;

    private String descripcion;

    private LocalDateTime fecha; // normalmente createdAt/fecha

    private Long usuarioId;

    // trazabilidad
    private Long ventaId;
    private Long pagoId;
    private Long servicioId;
    private Long productoId;
}
