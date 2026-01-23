package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import org.springframework.stereotype.Component;

@Component
public class CuadreCajaMapper {


    public CuadreCajaResponse toResponse(CuadreCaja entity) {

        if (entity == null) return null;

        Long sesionCajaId = null;
        if (entity.getSesionCaja() != null) {
            sesionCajaId = entity.getSesionCaja().getId();
        }

        return CuadreCajaResponse.builder()
                .id(entity.getId())
                .sesionCajaId(sesionCajaId)

                .efectivoEsperado(entity.getEfectivoEsperado())
                .efectivoContado(entity.getEfectivoContado())
                .diferencia(entity.getDiferencia())

                .estado(entity.getEstado() != null ? entity.getEstado().name() : null)

                .observacion(entity.getObservacion())

                // auditoría (de AuditedEntity)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
