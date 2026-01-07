package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembresiaClienteRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Integer clienteId;

    @NotNull(message = "La membresía es obligatoria")
    private Integer membresiaId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El estado de la membresía es obligatorio")
    private EstadoMembresia estado;

}
