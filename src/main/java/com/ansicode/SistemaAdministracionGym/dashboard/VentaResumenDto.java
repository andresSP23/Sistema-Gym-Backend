package com.ansicode.SistemaAdministracionGym.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class VentaResumenDto {
    private LocalDate fecha;
    private BigDecimal total;
}
