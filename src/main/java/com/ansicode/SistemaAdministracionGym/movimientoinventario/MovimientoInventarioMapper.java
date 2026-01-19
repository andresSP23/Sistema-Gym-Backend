package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.user.User;
import org.springframework.stereotype.Service;

@Service
public class MovimientoInventarioMapper {

        public MovimientoInventarioResponse toResponse(MovimientoInventario m) {
            MovimientoInventarioResponse r = new MovimientoInventarioResponse();
            r.setId(m.getId());
            r.setProducto(m.getProducto().getNombre());
            r.setTipo(m.getTipoMovimiento());
            r.setCantidad(m.getCantidad());
            r.setStockAnterior(m.getStockAnterior());
            r.setStockActual(m.getStockActual());
            r.setFecha(m.getCreatedAt());

            return r;
        }
}
