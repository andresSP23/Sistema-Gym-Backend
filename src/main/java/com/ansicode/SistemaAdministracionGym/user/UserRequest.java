package com.ansicode.SistemaAdministracionGym.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches

public class UserRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    @NotBlank(message = "Confirmar la contraseña es obligatorio")
    private String confirmPassword;


    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;




    @NotNull(message = "Los roles son obligatorios")
    private List<Integer> rolesIds;
}
