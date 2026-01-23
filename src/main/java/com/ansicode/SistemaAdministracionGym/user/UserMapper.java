package com.ansicode.SistemaAdministracionGym.user;

import com.ansicode.SistemaAdministracionGym.role.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .fullname(user.fullname())
                .telefono(user.getTelefono())
                .email(user.getEmail())
                .fechaNacimiento(user.getFechaNacimiento())
                .cuentaBloqueada(user.isCuentaBloqueada())
                .activa(user.isActiva())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .toList()
                )
                .fechaCreacion(user.getFechaCreacion())
                .fechaModificacion(user.getFechaModificacion())
                .build();
    }
}
