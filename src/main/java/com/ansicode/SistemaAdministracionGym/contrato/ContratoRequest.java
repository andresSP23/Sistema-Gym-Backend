package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.enums.EstadoContrato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContratoRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotBlank(message = "La URL del archivo es obligatoria")
    private String archivoUrl;

    @NotNull(message = "El estado del contrato es obligatorio")
    private EstadoContrato estadoContrato;

}
