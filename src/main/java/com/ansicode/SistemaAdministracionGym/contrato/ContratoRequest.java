package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.enums.EstadoContrato;

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
    private String archivoUrl;

    private EstadoContrato estadoContrato;

}
