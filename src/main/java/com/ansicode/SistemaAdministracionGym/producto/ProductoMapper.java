package com.ansicode.SistemaAdministracionGym.producto;

import org.springframework.stereotype.Service;

@Service
public class ProductoMapper {

    public Producto toProducto(ProductoRequest request) {
        Producto producto = new Producto();

        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setTipoProducto(request.getTipoProducto());

        return producto;
    }

    public void updateProducto(Producto producto, ProductoRequest request) {
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setTipoProducto(request.getTipoProducto());
    }

    public ProductoResponse toProductoResponse(Producto producto) {
        ProductoResponse response = new ProductoResponse();

        response.setId(producto.getId().intValue());
        response.setNombre(producto.getNombre());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());
        response.setTipoProducto(producto.getTipoProducto());

        // viene del BaseEntity
        response.setActivo(producto.getActivo());

        return response;
    }
}
