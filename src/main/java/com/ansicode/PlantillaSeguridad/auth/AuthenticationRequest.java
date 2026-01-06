package com.ansicode.PlantillaSeguridad.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {


    @NotEmpty(message = "El email es obligatorio")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email sin formato")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe contener almenos 6 caracteres")
    private String password;
}
