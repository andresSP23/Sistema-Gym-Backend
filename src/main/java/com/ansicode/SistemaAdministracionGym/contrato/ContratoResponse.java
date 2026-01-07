package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.enums.EstadoContrato;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContratoResponse {
    private Integer id;

    private Integer clienteId;
    private String clienteNombre; // nombres + apellidos del cliente

    private String archivoUrl;
    private EstadoContrato estadoContrato;

    private boolean activo;
}
