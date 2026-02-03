package com.ansicode.SistemaAdministracionGym.gasto;

import com.ansicode.SistemaAdministracionGym.enums.CategoriaGasto;
import com.ansicode.SistemaAdministracionGym.enums.EstadoGasto;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class GastoRequest {

    @NotNull(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaGasto categoria;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "La fecha del gasto es obligatoria")
    private LocalDate fechaGasto;

    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;

    // --- Pago inmediato (Opcional) ---
    private boolean pagarAhora;
    private MetodoPago metodoPago; // Requerido si pagarAhora = true
    private Long bancoId; // Requerido si metodoPago = TRANSFERENCIA
}
