package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVentaResponse;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaMapper {


    public VentaResponse toResponse(Venta venta) {
        if (venta == null) return null;

        return VentaResponse.builder()
                .id(venta.getId())
                .numeroFactura(venta.getNumeroFactura())
                .fechaVenta(venta.getFechaVenta())
                .clienteId(venta.getCliente() != null ? venta.getCliente().getId() : null)
                .sucursalId(venta.getSucursal() != null ? venta.getSucursal().getId() : null)
                .cajeroUsuarioId(venta.getCajeroUsuario() != null ? venta.getCajeroUsuario().getId() : null)
                .estado(venta.getEstado())
                .subtotal(venta.getSubtotal())
                .descuentoTotal(venta.getDescuentoTotal())
                .impuestoTotal(venta.getImpuestoTotal())
                .total(venta.getTotal())
                .detalles(venta.getDetalles() == null
                        ? new ArrayList<>()
                        : venta.getDetalles().stream().map(this::toDetalleResponse).toList()
                )
                .build();
    }

    private DetalleVentaResponse toDetalleResponse(DetalleVenta det) {
        return DetalleVentaResponse.builder()
                .id(det.getId())
                .tipoItem(det.getTipoItem())
                .referenciaId(det.getReferenciaId())
                .descripcionSnapshot(det.getDescripcionSnapshot())
                .precioUnitarioSnapshot(det.getPrecioUnitarioSnapshot())
                .cantidad(det.getCantidad())
                .descuento(det.getDescuento())
                .impuesto(det.getImpuesto())
                .totalLinea(det.getTotalLinea())
                .build();
    }

}
