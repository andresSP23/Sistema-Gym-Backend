package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CancelarSuscripcionRequest {
    private String motivo;

    // Opcionales para devolución
    private Boolean devolverDinero;
    private BigDecimal montoDevolucion;
    private MetodoPago metodoDevolucion;

    // Requerido si metodoDevolucion == TRANSFERENCIA || TARJETA
    private Long bancoId;
}
