package com.ansicode.SistemaAdministracionGym.conteocaja;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
@Component
public class ConteoCajaMapper {

    public ConteoCajaItemResponse toItemResponse(ConteoCaja entity) {
        if (entity == null) return null;

        return ConteoCajaItemResponse.builder()
                .id(entity.getId())
                .moneda(entity.getMoneda())
                .denominacion(entity.getDenominacion())
                .cantidad(entity.getCantidad())
                .subtotal(entity.getSubtotal())
                .build();
    }

    public ConteoCajaResponse toResponse(Long sesionCajaId, List<ConteoCaja> items) {

        BigDecimal total = items.stream()
                .map(ConteoCaja::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // si hay items, tomo la moneda del primero (asumiendo misma moneda)
        String moneda = items.isEmpty() ? "USD" : items.get(0).getMoneda();

        return ConteoCajaResponse.builder()
                .sesionCajaId(sesionCajaId)
                .moneda(moneda)
                .items(items.stream().map(this::toItemResponse).toList())
                .totalContado(total)
                .build();
    }
}
