package com.ansicode.SistemaAdministracionGym.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MembresiaPagoDto {

    private String nombreCliente;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
}
