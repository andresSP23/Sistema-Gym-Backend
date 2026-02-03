package com.ansicode.SistemaAdministracionGym.contrato.plantilla;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlantillaContratoResponse {
    private Long id;
    private String titulo;
    private String contenido;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
