package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Integer clienteId;

    @NotNull(message = "El vendedor es obligatorio")
    private Integer usuarioId;

    @NotNull(message = "El total de la venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    private BigDecimal total;

    @NotNull(message = "El estado de la venta es obligatorio")
    private EstadoVenta estadoVenta;

    @NotNull(message = "La fecha de venta es obligatoria")
    private LocalDateTime fechaVenta;
}
