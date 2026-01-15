package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.validation.cedula.CedulaEcuatoriana;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ClienteRequest {
    @NotBlank(message = "La cédula es obligatoria")
    @CedulaEcuatoriana
    private String cedula;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 50, message = "Los nombres no pueden exceder 50 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$",
            message = "Los nombres solo pueden contener letras"
    )
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 50, message = "Los apellidos no pueden exceder 50 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$",
            message = "Los apellidos solo pueden contener letras"
    )
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    @Size(max = 50, message = "El email no puede exceder 50 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(
            regexp = "^[0-9+ ]+$",
            message = "El teléfono solo puede contener números"
    )
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 120, message = "La edad no puede ser mayor a 120 años")
    private Integer edad;
}
