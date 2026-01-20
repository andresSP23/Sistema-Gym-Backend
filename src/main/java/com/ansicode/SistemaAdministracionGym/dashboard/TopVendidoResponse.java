package com.ansicode.SistemaAdministracionGym.dashboard;

import java.math.BigDecimal;

public record TopVendidoResponse(
        String nombre,
        BigDecimal cantidad,
        BigDecimal total
) {
}
