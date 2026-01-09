package com.ansicode.SistemaAdministracionGym.producto;

import org.springframework.stereotype.Service;

@Service
public class ProductoMapper {

    public Producto toEntity(ProductoRequest request) {
        Producto p = new Producto();
        p.setNombre(request.getNombre());
        p.setPrecio(request.getPrecio());
        p.setTipoProducto(request.getTipoProducto());
        p.setStock(0);
        return p;
    }

    public ProductoResponse toResponse(Producto p) {
        ProductoResponse r = new ProductoResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setPrecio(p.getPrecio());
        r.setStock(p.getStock());
        r.setTipoProducto(p.getTipoProducto());
        r.setActivo(p.getActivo());
        return r;
    }

    public void updateEntity(Producto producto, ProductoRequest request) {
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setTipoProducto(request.getTipoProducto());

    }
}
