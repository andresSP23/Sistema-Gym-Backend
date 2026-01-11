package com.ansicode.SistemaAdministracionGym.asistencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaRequest {


    @NotBlank(message = "La cédula del cliente es obligatoria")
    private String cedulaCliente;

    @NotNull(message = "La fecha de entrada es obligatoria")
    private LocalDateTime fechaEntrada;
}
