package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import org.springframework.stereotype.Component;

@Component
public class CategoriaProductoMapper {


    public CategoriaProducto toEntity(CategoriaProductoRequest request) {
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return categoria;
    }

    public CategoriaProductoResponse toResponse(CategoriaProducto categoria) {
        CategoriaProductoResponse response = new CategoriaProductoResponse();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        response.setActivo(categoria.getIsVisible());
        return response;
    }

    public void updateEntity(
            CategoriaProducto categoria,
            CategoriaProductoRequest request
    ) {
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
    }
}
