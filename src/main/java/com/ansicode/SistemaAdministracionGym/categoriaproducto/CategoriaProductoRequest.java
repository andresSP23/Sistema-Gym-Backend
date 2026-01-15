package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ]+$",
            message = "El nombre solo puede contener letras y números"
    )
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
}
