package com.ansicode.SistemaAdministracionGym.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SerieResponse(
        LocalDate fecha,
        BigDecimal total
) {

}
