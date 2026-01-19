package com.ansicode.SistemaAdministracionGym.movimientodinero;

import org.springframework.stereotype.Component;

@Component
public class MovimientoDineroMapper {

    public MovimientoDineroResponse toResponse(MovimientoDinero m) {
        if (m == null) return null;

        return MovimientoDineroResponse.builder()
                .id(m.getId())
                .sesionCajaId(m.getSesionCaja() != null ? m.getSesionCaja().getId() : null)
                .tipo(m.getTipo())
                .concepto(m.getConcepto())
                .metodo(m.getMetodo())
                .moneda(m.getMoneda())
                .monto(m.getMonto())
                .descripcion(m.getDescripcion())
                .fecha(m.getFecha()) // o m.getCreatedAt() si usas auditing
                .usuarioId(m.getUsuarioId())
                .ventaId(m.getVenta() != null ? m.getVenta().getId() : null)
                .pagoId(m.getPago() != null ? m.getPago().getId() : null)
                .servicioId(m.getServicio() != null ? m.getServicio().getId() : null)
                .productoId(m.getProductoId())
                .build();
    }
}
