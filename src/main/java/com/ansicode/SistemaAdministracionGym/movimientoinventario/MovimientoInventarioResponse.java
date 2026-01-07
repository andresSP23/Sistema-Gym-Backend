package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoInventario;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoInventarioResponse {
    private Integer id;

    private Integer productoId;
    private String productoNombre;

    private Integer cantidad;
    private TipoMovimientoInventario tipoMovimiento;

    private Integer usuarioId;
    private String usuarioNombre;

    private LocalDateTime fechaMovimiento;

    private boolean activo;
}
