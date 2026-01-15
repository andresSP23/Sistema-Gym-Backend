package com.ansicode.SistemaAdministracionGym.asistencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaRequest {


    @NotBlank(message = "La cédula del cliente es obligatoria")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "La cédula debe tener exactamente 10 dígitos"
    )
    private String cedulaCliente;

//    @NotNull(message = "La fecha de entrada es obligatoria")
//    @PastOrPresent(message = "La fecha de entrada no puede ser futura")
//    private LocalDateTime fechaEntrada;
}
