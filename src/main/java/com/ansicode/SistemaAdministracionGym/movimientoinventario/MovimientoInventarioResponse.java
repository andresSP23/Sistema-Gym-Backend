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
    private Long id;
    private String producto;
    private TipoMovimientoInventario tipo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockActual;
    private LocalDateTime fecha;
}
