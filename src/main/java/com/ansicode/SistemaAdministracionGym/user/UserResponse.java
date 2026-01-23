package com.ansicode.SistemaAdministracionGym.user;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;

    private String nombre;
    private String apellido;
    private String fullname; // nombre + apellido
    private String telefono;
    private String email;

    private LocalDate fechaNacimiento;

    private Boolean cuentaBloqueada;
    private Boolean activa;

    private List<String> roles; // nombres de roles asignados

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;


}
