package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.producto.Producto;
import org.springframework.stereotype.Service;

@Service
public class DetalleVentaMapper {

    public DetalleVenta toDetalleVenta(DetalleVentaRequest request, Venta venta, Producto producto) {
        return DetalleVenta.builder()
                .venta(venta)
                .producto(producto)
                .cantidad(request.getCantidad())
                .precioUnitario(request.getPrecioUnitario())
                .subtotal(request.getSubtotal())
                .build();
    }

    public DetalleVentaResponse toDetalleVentaResponse(DetalleVenta detalleVenta) {

        DetalleVentaResponse response = new DetalleVentaResponse();

        response.setId(detalleVenta.getId());

        response.setVentaId(detalleVenta.getVenta().getId());
        response.setVentaTotal(detalleVenta.getVenta().getTotal());

        response.setProductoId(detalleVenta.getProducto().getId());
        response.setProductoNombre(detalleVenta.getProducto().getNombre());

        response.setCantidad(detalleVenta.getCantidad());
        response.setPrecioUnitario(detalleVenta.getPrecioUnitario());
        response.setSubtotal(detalleVenta.getSubtotal());

        response.setActivo(detalleVenta.getActivo());

        return response;
    }
}
