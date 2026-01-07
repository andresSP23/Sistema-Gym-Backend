package com.ansicode.SistemaAdministracionGym.cliente;

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
    @Size(min = 10, message = "La cédula debe tener 10 caracteres ")
    private String cedula;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 50, message = "Los nombres no pueden exceder 50 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 50, message = "Los apellidos no pueden exceder 50 caracteres")
    private String apellidos;

    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El email es obligatorio")
    @Size(max = 50, message = "El email no puede exceder 50 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @Size(max = 50, message = "El código interno no puede exceder 50 caracteres")
    private String codigoInterno;

    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDate fechaRegistro;
}
