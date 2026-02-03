package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.enums.EstadoContrato;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContratoResponse {
    private Long id;

    private Long clienteId;
    private String clienteNombre; // nombres + apellidos del cliente

    private String archivoUrl;
    private String contenidoContrato;
    private EstadoContrato estadoContrato;

    private boolean activo;

    private java.time.LocalDateTime fechaFirma;
    private java.time.LocalDateTime fechaGeneracion;
}
