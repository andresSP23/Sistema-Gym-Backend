package com.ansicode.SistemaAdministracionGym.sesioncaja;

import org.springframework.stereotype.Component;

@Component
public class SesionCajaMapper {

    public SesionCajaResponse toResponse(SesionCaja s) {
        return SesionCajaResponse.builder()
                .id(s.getId())
                .sucursalId(s.getSucursalId())
                .usuarioAperturaId(s.getUsuarioAperturaId())
                .fechaApertura(s.getFechaApertura())
                .baseInicialEfectivo(s.getBaseInicialEfectivo())
                .estado(s.getEstado())
                .fechaCierre(s.getFechaCierre())
                .usuarioCierreId(s.getUsuarioCierreId())
                .observacion(s.getObservacion())
                .build();
    }
}
