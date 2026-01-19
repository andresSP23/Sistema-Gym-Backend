package com.ansicode.SistemaAdministracionGym.detalleventa;

import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DetalleVentaMapper {

//
//    public DetalleVenta toDetalleVenta(
//            Venta venta,
//            Producto producto,
//            Integer cantidad,
//            BigDecimal precioUnitario,
//            BigDecimal subtotal
//    ) {
//        DetalleVenta detalle = new DetalleVenta();
//        detalle.setVenta(venta);
//        detalle.setProducto(producto);
//        detalle.setCantidad(cantidad);
//        detalle.setPrecioUnitario(precioUnitario);
//        detalle.setSubtotal(subtotal);
//        detalle.setActivo(true);
//        return detalle;
//    }
//
//    public DetalleVentaResponse toDetalleVentaResponse(DetalleVenta detalle) {
//        return DetalleVentaResponse.builder()
//                .productoId(detalle.getProducto().getId())
//                .productoNombre(detalle.getProducto().getNombre())
//                .cantidad(detalle.getCantidad())
//                .precioUnitario(detalle.getPrecioUnitario())
//                .subtotal(detalle.getSubtotal())
//                .build();
//    }
}
