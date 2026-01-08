package com.ansicode.SistemaAdministracionGym.asistencia;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaRequest {


    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "La fecha de entrada es obligatoria")
    private LocalDateTime fechaEntrada;
}
