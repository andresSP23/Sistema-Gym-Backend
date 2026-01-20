package com.ansicode.SistemaAdministracionGym.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public record DashBoardResumenResponse(

        BigDecimal ingresosTotales,
        BigDecimal egresosTotales,
        BigDecimal gananciaTotal,

        BigDecimal ingresosHoy,
        BigDecimal egresosHoy,
        BigDecimal gananciaHoy,

        Long numeroClientes,
        BigDecimal productosVendidosCantidad,
        BigDecimal serviciosVendidosCantidad
) {
}
