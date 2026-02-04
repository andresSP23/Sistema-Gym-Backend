package com.ansicode.SistemaAdministracionGym.gasto;

import com.ansicode.SistemaAdministracionGym.enums.CategoriaGasto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GastoMapper {

    public Gasto toEntity(GastoRequest request) {
        BigDecimal montoFinal = calcularMonto(request);

        return Gasto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .monto(montoFinal)
                .fechaGasto(request.getFechaGasto())
                .sucursalId(request.getSucursalId())
                .cantidad(request.getCantidad())
                .precioUnitario(request.getPrecioUnitario())
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
                .cantidad(entity.getCantidad())
                .precioUnitario(entity.getPrecioUnitario())
                .build();
    }

    public void updateEntity(Gasto entity, GastoRequest request) {
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setCategoria(request.getCategoria());
        entity.setMonto(calcularMonto(request));
        entity.setFechaGasto(request.getFechaGasto());
        entity.setCantidad(request.getCantidad());
        entity.setPrecioUnitario(request.getPrecioUnitario());
        // No actualizamos sucursal ni estado pago aqui
    }

    /**
     * Calcula el monto automático para SUMINISTROS cuando se proporciona cantidad y
     * precioUnitario.
     * De lo contrario, usa el monto enviado directamente.
     */
    private BigDecimal calcularMonto(GastoRequest request) {
        // Si es SUMINISTROS y se proporcionan cantidad + precioUnitario, calcular
        if (request.getCategoria() == CategoriaGasto.SUMINISTROS
                && request.getCantidad() != null && request.getCantidad().compareTo(BigDecimal.ZERO) > 0
                && request.getPrecioUnitario() != null && request.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0) {
            return request.getCantidad().multiply(request.getPrecioUnitario());
        }
        // De lo contrario, usar monto directo
        return request.getMonto();
    }
}
