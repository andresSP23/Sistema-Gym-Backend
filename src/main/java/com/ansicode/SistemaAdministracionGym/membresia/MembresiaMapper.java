package com.ansicode.SistemaAdministracionGym.membresia;

import org.springframework.stereotype.Service;

@Service
public class MembresiaMapper {

    // Crear entidad desde request
    public Membresia toMembresia(MembresiaRequest request) {
        return Membresia.builder()
                .nombre(request.getNombre())
                .duracionDias(request.getDuracionDias())
                .precio(request.getPrecio())
                .descripcion(request.getDescripcion())
                .permiteCongelacion(request.getPermiteCongelacion())
                .build();
    }

    // Convertir entidad a response
    public MembresiaResponse toMembresiaResponse(Membresia membresia) {
        return MembresiaResponse.builder()
                .id(membresia.getId())
                .nombre(membresia.getNombre())
                .duracionDias(membresia.getDuracionDias())
                .precio(membresia.getPrecio())
                .descripcion(membresia.getDescripcion())
                .permiteCongelacion(membresia.getPermiteCongelacion())
                .activo(membresia.getActivo())
                .build();
    }

    // Actualizar entidad desde request
    public void updateMembresiaFromRequest(Membresia membresia, MembresiaRequest request) {
        membresia.setNombre(request.getNombre());
        membresia.setDuracionDias(request.getDuracionDias());
        membresia.setPrecio(request.getPrecio());
        membresia.setDescripcion(request.getDescripcion());
        membresia.setPermiteCongelacion(request.getPermiteCongelacion());
    }

}
