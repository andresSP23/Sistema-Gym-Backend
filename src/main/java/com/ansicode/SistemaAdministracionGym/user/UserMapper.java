package com.ansicode.SistemaAdministracionGym.user;

import com.ansicode.SistemaAdministracionGym.role.Role;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserMapper {

    // -------------------------
    // Entity -> Response
    // -------------------------
    public UserResponse toResponse(User user) {
        if (user == null) return null;

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
                .roles(user.getRoles() == null
                        ? Collections.emptyList()
                        : user.getRoles().stream()
                        .map(Role::getName)
                        .toList()
                )
                .fechaCreacion(user.getFechaCreacion())
                .fechaModificacion(user.getFechaModificacion())
                .build();
    }

    // -------------------------
    // Request -> Entity (CREATE)
    // -------------------------
    public User toEntity(UserRequest request, List<Role> rolesResolved) {
        if (request == null) return null;

        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setTelefono(request.getTelefono());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFechaNacimiento(request.getFechaNacimiento());

        user.setRoles(rolesResolved == null ? Collections.emptyList() : rolesResolved);

        user.setActiva(true);
        user.setCuentaBloqueada(false);

        return user;
    }



    public void mapProfileForUpdate(User target, UserUpdateRequest request) {
        // El service garantiza qué valores se mandan (null o no)

        target.setNombre(request.getNombre());
        target.setApellido(request.getApellido());
        target.setTelefono(request.getTelefono());
        target.setFechaNacimiento(request.getFechaNacimiento());
    }
}
