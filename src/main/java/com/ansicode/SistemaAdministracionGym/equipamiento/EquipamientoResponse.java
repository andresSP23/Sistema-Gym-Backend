package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.enums.EstadoEquipamiento;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipamientoResponse {
    private Integer id;
    private String nombre;
    private String ubicacion;
    private EstadoEquipamiento estadoEquipamiento;
    private String fotoUrl;
    private boolean activo;
}
