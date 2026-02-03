package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.enums.EstadoEquipamiento;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipamientoResponse {
    private Long id;
    private String nombre;
    private String ubicacion;
    private EstadoEquipamiento estadoEquipamiento;
    private String fotoUrl;
    private boolean activo;

    private String marca;
    private String modelo;
    private String numeroSerie;
    private java.time.LocalDate fechaCompra;
    private java.math.BigDecimal costo;
    private String proveedor;
    private java.time.LocalDate garantiaFin;
    private Integer frecuenciaMantenimientoDias;
    private java.time.LocalDate proximoMantenimiento;
}
