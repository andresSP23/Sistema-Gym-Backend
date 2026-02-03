package com.ansicode.SistemaAdministracionGym.contrato.plantilla;

import org.springframework.stereotype.Service;

@Service
public class PlantillaContratoMapper {

    public PlantillaContrato toEntity(PlantillaContratoRequest request) {
        return PlantillaContrato.builder()
                .titulo(request.getTitulo())
                .contenido(request.getContenido())
                .activo(request.isActivo())
                .build();
    }

    public PlantillaContratoResponse toResponse(PlantillaContrato entity) {
        return PlantillaContratoResponse.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .contenido(entity.getContenido())
                .activo(entity.isActivo())
                .fechaCreacion(entity.getCreatedAt())
                .fechaModificacion(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(PlantillaContrato entity, PlantillaContratoRequest request) {
        entity.setTitulo(request.getTitulo());
        entity.setContenido(request.getContenido());
        entity.setActivo(request.isActivo());
    }
}
