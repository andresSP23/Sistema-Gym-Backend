package com.ansicode.SistemaAdministracionGym.mantenimiento;

import com.ansicode.SistemaAdministracionGym.enums.TipoMantenimiento;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MantenimientoRequest {

    @NotNull
    private Long equipamientoId;

    @NotNull
    private LocalDateTime fechaRealizacion;

    @NotNull
    private TipoMantenimiento tipo;

    private BigDecimal costo;
    private String descripcion;
    private String tecnico;

    // Campos de pago
    private com.ansicode.SistemaAdministracionGym.enums.MetodoPago metodoPago;
    private Long bancoId; // opcional para transferencias/tarjetas
    private String moneda = "USD";
}
