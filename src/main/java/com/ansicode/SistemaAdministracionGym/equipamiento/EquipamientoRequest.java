package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.enums.EstadoEquipamiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipamientoRequest {

    @NotBlank(message = "El nombre del equipamiento es obligatorio")
    private String nombre;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotNull(message = "El estado del equipamiento es obligatorio")
    private EstadoEquipamiento estadoEquipamiento;

    @NotBlank(message = "La URL de la foto es obligatoria")
    private String fotoUrl;
}
