package com.ansicode.SistemaAdministracionGym.mantenimiento;

import com.ansicode.SistemaAdministracionGym.enums.TipoMantenimiento;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MantenimientoResponse {
    private Long id;
    private Long equipamientoId;
    private String equipamientoNombre; // Useful for lists
    private LocalDateTime fechaRealizacion;
    private TipoMantenimiento tipo;
    private BigDecimal costo;
    private String descripcion;
    private String tecnico;
    private LocalDate proximoMantenimientoSugerido;
}
