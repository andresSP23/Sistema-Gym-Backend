package com.ansicode.SistemaAdministracionGym.gasto;

import org.springframework.stereotype.Service;

@Service
public class GastoMapper {

    public Gasto toEntity(GastoRequest request) {
        return Gasto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .monto(request.getMonto())
                .fechaGasto(request.getFechaGasto())
                .sucursalId(request.getSucursalId())
                // El estado se define en el servicio según si "pagarAhora" es true o false
                .build();
    }

    public GastoResponse toResponse(Gasto entity) {
        return GastoResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .categoria(entity.getCategoria())
                .monto(entity.getMonto())
                .fechaGasto(entity.getFechaGasto())
                .estado(entity.getEstado())
                .fechaPago(entity.getFechaPago())
                .metodoPago(entity.getMetodoPago())
                .sucursalId(entity.getSucursalId())
                .build();
    }

    public void updateEntity(Gasto entity, GastoRequest request) {
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setCategoria(request.getCategoria());
        entity.setMonto(request.getMonto());
        entity.setFechaGasto(request.getFechaGasto());
        // No actualizamos sucursal ni estado pago aqui
    }
}
