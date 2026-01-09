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

    @NotNull
    private Long productoId;

    @NotNull
    private TipoMovimientoInventario tipo;

    @NotNull
    @Min(1)
    private Integer cantidad;
}
