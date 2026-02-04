package com.ansicode.SistemaAdministracionGym.equipamiento;

import org.springframework.stereotype.Service;

@Service
public class EquipamientoMapper {

    // Mapear request a entidad (crear)
    public Equipamiento toEquipamiento(EquipamientoRequest request) {
        Equipamiento equip = Equipamiento.builder()
                .nombre(request.getNombre())
                .ubicacion(request.getUbicacion())
                .estadoEquipamiento(request.getEstadoEquipamiento())
                .fotoUrl(request.getFotoUrl())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .numeroSerie(request.getNumeroSerie())
                .fechaCompra(request.getFechaCompra())
                .costo(request.getCosto())
                .proveedor(request.getProveedor())
                .garantiaFin(request.getGarantiaFin())
                .frecuenciaMantenimientoDias(request.getFrecuenciaMantenimientoDias())
                .proximoMantenimiento(request.getProximoMantenimiento())
                .build();

        // Auto-calculate next maintenance if frequency is set but date is missing
        if (equip.getProximoMantenimiento() == null && equip.getFrecuenciaMantenimientoDias() != null
                && equip.getFrecuenciaMantenimientoDias() > 0) {
            java.time.LocalDate baseDate = equip.getFechaCompra() != null ? equip.getFechaCompra()
                    : java.time.LocalDate.now();
            equip.setProximoMantenimiento(baseDate.plusDays(equip.getFrecuenciaMantenimientoDias()));
        }

        return equip;
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
                .marca(equipamiento.getMarca())
                .modelo(equipamiento.getModelo())
                .numeroSerie(equipamiento.getNumeroSerie())
                .fechaCompra(equipamiento.getFechaCompra())
                .costo(equipamiento.getCosto())
                .proveedor(equipamiento.getProveedor())
                .garantiaFin(equipamiento.getGarantiaFin())
                .frecuenciaMantenimientoDias(equipamiento.getFrecuenciaMantenimientoDias())
                .proximoMantenimiento(equipamiento.getProximoMantenimiento())
                .build();
    }

    // Mapear actualización
    public void updateEquipamientoFromRequest(Equipamiento equipamiento, EquipamientoRequest request) {
        equipamiento.setNombre(request.getNombre());
        equipamiento.setUbicacion(request.getUbicacion());
        equipamiento.setEstadoEquipamiento(request.getEstadoEquipamiento());
        equipamiento.setFotoUrl(request.getFotoUrl()); // fotoUrl is optional
        equipamiento.setMarca(request.getMarca());
        equipamiento.setModelo(request.getModelo());
        equipamiento.setNumeroSerie(request.getNumeroSerie());
        equipamiento.setFechaCompra(request.getFechaCompra());
        equipamiento.setCosto(request.getCosto());
        equipamiento.setProveedor(request.getProveedor());
        equipamiento.setGarantiaFin(request.getGarantiaFin());
        equipamiento.setFrecuenciaMantenimientoDias(request.getFrecuenciaMantenimientoDias());

        // Auto-calculate próximo mantenimiento if frequency is set but
        // proximoMantenimiento is not provided
        if (request.getProximoMantenimiento() != null) {
            equipamiento.setProximoMantenimiento(request.getProximoMantenimiento());
        } else if (request.getFrecuenciaMantenimientoDias() != null
                && request.getFrecuenciaMantenimientoDias() > 0) {
            // Recalculate based on last maintenance or purchase date or today
            java.time.LocalDate baseDate = equipamiento.getProximoMantenimiento() != null
                    ? equipamiento.getProximoMantenimiento()
                    : (equipamiento.getFechaCompra() != null ? equipamiento.getFechaCompra()
                            : java.time.LocalDate.now());
            equipamiento.setProximoMantenimiento(baseDate.plusDays(request.getFrecuenciaMantenimientoDias()));
        }
    }
}
