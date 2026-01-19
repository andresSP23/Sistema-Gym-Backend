package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProducto;
import org.springframework.stereotype.Service;

@Service
public class ProductoMapper {

    public Producto toEntity(
            ProductoRequest request,
            CategoriaProducto categoria
    ) {
        Producto p = new Producto();
        p.setNombre(request.getNombre());
        p.setPrecioCompra(request.getPrecioCompra());
        p.setPrecioVenta(request.getPrecioVenta());
        p.setCategoriaProducto(categoria);
        return p;
    }

    public ProductoResponse toProductoResponse(Producto p) {
        ProductoResponse r = new ProductoResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setPrecioCompra(p.getPrecioCompra());
        r.setPrecioVenta(p.getPrecioVenta());
        r.setGanancia(p.getGanancia());
        r.setStock(p.getStock());

        r.setCategoriaProductoId(p.getCategoriaProducto().getId());
        r.setCategoriaProductoNombre(p.getCategoriaProducto().getNombre());

        r.setActivo(p.getIsVisible());
        return r;
    }
}