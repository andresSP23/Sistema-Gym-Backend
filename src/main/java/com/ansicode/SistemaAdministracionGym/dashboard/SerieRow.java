package com.ansicode.SistemaAdministracionGym.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SerieRow
{
    LocalDate getFecha();
    BigDecimal getTotal();
}
