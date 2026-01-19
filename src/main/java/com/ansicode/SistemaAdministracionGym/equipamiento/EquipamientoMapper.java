package com.ansicode.SistemaAdministracionGym.equipamiento;

import org.springframework.stereotype.Service;

@Service
public class EquipamientoMapper {

    // Mapear request a entidad (crear)
    public Equipamiento toEquipamiento(EquipamientoRequest request) {
        return Equipamiento.builder()
                .nombre(request.getNombre())
                .ubicacion(request.getUbicacion())
                .estadoEquipamiento(request.getEstadoEquipamiento())
                .fotoUrl(request.getFotoUrl())
                .build();
    }

    // Mapear entidad a response
    public EquipamientoResponse toEquipamientoResponse(Equipamiento equipamiento) {
        return EquipamientoResponse.builder()
                .id(equipamiento.getId())
                .nombre(equipamiento.getNombre())
                .ubicacion(equipamiento.getUbicacion())
                .estadoEquipamiento(equipamiento.getEstadoEquipamiento())
                .fotoUrl(equipamiento.getFotoUrl())
                .activo(equipamiento.getIsVisible())
                .build();
    }

    // Mapear actualización
    public void updateEquipamientoFromRequest(Equipamiento equipamiento, EquipamientoRequest request) {
        equipamiento.setNombre(request.getNombre());
        equipamiento.setUbicacion(request.getUbicacion());
        equipamiento.setEstadoEquipamiento(request.getEstadoEquipamiento());
        equipamiento.setFotoUrl(request.getFotoUrl());
    }
}
