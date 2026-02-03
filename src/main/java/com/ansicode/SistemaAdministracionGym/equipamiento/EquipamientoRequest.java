package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.enums.EstadoEquipamiento;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipamientoRequest {

    @NotBlank(message = "El nombre del equipamiento es obligatorio")
    private String nombre;

    private String ubicacion;

    private EstadoEquipamiento estadoEquipamiento;

    private String fotoUrl;

    private String marca;
    private String modelo;
    private String numeroSerie;

    private java.time.LocalDate fechaCompra;
    private java.math.BigDecimal costo;
    private String proveedor;
    private java.time.LocalDate garantiaFin;

    private Integer frecuenciaMantenimientoDias;
    private java.time.LocalDate proximoMantenimiento;

    // --- Financial Integration ---
    private com.ansicode.SistemaAdministracionGym.enums.MetodoPago metodoPago;
    private Long bancoId; // Required if MetodoPago == TRANSFERENCIA
    private Long sucursalId; // Required if MetodoPago == EFECTIVO
}
