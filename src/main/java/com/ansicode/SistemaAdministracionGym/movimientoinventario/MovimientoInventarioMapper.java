package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.user.User;
import org.springframework.stereotype.Service;

@Service
public class MovimientoInventarioMapper {
    // Crear entidad desde request
    public MovimientoInventario toMovimientoInventario(MovimientoInventarioRequest request, Producto producto, User usuario) {
        return MovimientoInventario.builder()
                .producto(producto)
                .cantidad(request.getCantidad())
                .tipoMovimiento(request.getTipoMovimiento())
                .usuario(usuario)
                .fechaMovimiento(request.getFechaMovimiento())
                .build();
    }

    // Convertir entidad a response
    public MovimientoInventarioResponse toMovimientoInventarioResponse(MovimientoInventario movimientoInventario) {
        return MovimientoInventarioResponse.builder()
                .id(movimientoInventario.getId())
                .productoId(movimientoInventario.getProducto().getId())
                .productoNombre(movimientoInventario.getProducto().getNombre())
                .cantidad(movimientoInventario.getCantidad())
                .tipoMovimiento(movimientoInventario.getTipoMovimiento())
                .usuarioId(movimientoInventario.getUsuario().getId())
                .usuarioNombre(movimientoInventario.getUsuario().fullname())
                .fechaMovimiento(movimientoInventario.getFechaMovimiento())
                .activo(movimientoInventario.getActivo())
                .build();
    }

    // Actualizar entidad desde request
    public void updateMovimientoInventarioFromRequest(MovimientoInventario movimientoInventario, MovimientoInventarioRequest request, Producto producto, User usuario) {
        movimientoInventario.setProducto(producto);
        movimientoInventario.setCantidad(request.getCantidad());
        movimientoInventario.setTipoMovimiento(request.getTipoMovimiento());
        movimientoInventario.setUsuario(usuario);
        movimientoInventario.setFechaMovimiento(request.getFechaMovimiento());
    }
}
