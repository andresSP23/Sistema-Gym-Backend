package com.ansicode.SistemaAdministracionGym.gasto;

import com.ansicode.SistemaAdministracionGym.enums.CategoriaGasto;
import com.ansicode.SistemaAdministracionGym.enums.EstadoGasto;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GastoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private CategoriaGasto categoria;
    private BigDecimal monto;
    private LocalDate fechaGasto;
    private EstadoGasto estado;
    private LocalDate fechaPago;
    private MetodoPago metodoPago;
    private Long sucursalId;

    // Campos para SUMINISTROS
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
}
