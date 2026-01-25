package com.ansicode.SistemaAdministracionGym.user;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@PasswordMatchesUpdate
public class UserUpdateRequest {


    // -------- Perfil --------
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;

    @Size(min = 7, max = 20, message = "El teléfono debe ser válido")
    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser pasada")
    private LocalDate fechaNacimiento;

    // -------- Seguridad --------
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password; // opcional

    private String confirmPassword; // opcional


    // -------- Administración (solo admin) --------
    private List<Integer> rolesIds; // opcional
}
