package com.ansicode.SistemaAdministracionGym.servicio;

import org.springframework.stereotype.Component;

@Component
public class ServicioMapper {

    public ServiciosResponse toResponse(Servicios s) {
        return ServiciosResponse.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .duracionDias(s.getDuracionDias())
                .precio(s.getPrecio())
                .esSuscripcion(s.isEsSuscripcion())
                .estado(s.isEstado())
                .build();
    }

    public Servicios toEntity(ServiciosRequest r) {
        Servicios s = new Servicios();
        mapToEntity(s, r);
        return s;
    }

    public void mapToEntity(Servicios s, ServiciosRequest r) {
        s.setNombre(r.getNombre());
        s.setDescripcion(r.getDescripcion());
        s.setDuracionDias(r.getDuracionDias());
        s.setPrecio(r.getPrecio());
        s.setEsSuscripcion(r.getEsSuscripcion());
        s.setEstado(r.getEstado() != null ? r.getEstado() : true);
    }
}
