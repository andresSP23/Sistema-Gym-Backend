package com.ansicode.SistemaAdministracionGym.mantenimiento;

import com.ansicode.SistemaAdministracionGym.equipamiento.Equipamiento;
import org.springframework.stereotype.Service;

@Service
public class MantenimientoMapper {

    public Mantenimiento toEntity(MantenimientoRequest request, Equipamiento equipamiento) {
        return Mantenimiento.builder()
                .equipamiento(equipamiento)
                .fechaRealizacion(request.getFechaRealizacion())
                .tipo(request.getTipo())
                .costo(request.getCosto())
                .descripcion(request.getDescripcion())
                .tecnico(request.getTecnico())
                .build();
    }

    public MantenimientoResponse toResponse(Mantenimiento entity) {
        return MantenimientoResponse.builder()
                .id(entity.getId())
                .equipamientoId(entity.getEquipamiento().getId())
                .equipamientoNombre(entity.getEquipamiento().getNombre())
                .fechaRealizacion(entity.getFechaRealizacion())
                .tipo(entity.getTipo())
                .costo(entity.getCosto())
                .descripcion(entity.getDescripcion())
                .tecnico(entity.getTecnico())
                .proximoMantenimientoSugerido(entity.getProximoMantenimientoSugerido())
                .build();
    }
}
