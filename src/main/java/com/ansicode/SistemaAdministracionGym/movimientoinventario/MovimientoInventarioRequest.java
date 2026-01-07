package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoInventario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoInventarioRequest {

    @NotNull(message = "El producto es obligatorio")
    private Integer productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimientoInventario tipoMovimiento;

    @NotNull(message = "El usuario que realiza el movimiento es obligatorio")
    private Integer usuarioId;

    @NotNull(message = "La fecha del movimiento es obligatoria")
    private LocalDateTime fechaMovimiento;
}
