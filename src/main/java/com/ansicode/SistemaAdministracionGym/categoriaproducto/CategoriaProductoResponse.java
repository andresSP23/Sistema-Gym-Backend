package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaProductoResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;
}
