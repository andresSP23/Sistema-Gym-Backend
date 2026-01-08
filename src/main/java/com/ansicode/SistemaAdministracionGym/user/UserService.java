package com.ansicode.SistemaAdministracionGym.user;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.role.Role;
import com.ansicode.SistemaAdministracionGym.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponse create(UserRequest request , Authentication connectedUser) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("El email ya está registrado");
        }

        List<Role> roles = roleRepository.findAllById(request.getRolesIds());

        if (roles.isEmpty()) {
            throw new IllegalStateException("Debe asignar al menos un rol");
        }

        User user = User.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fechaNacimiento(request.getFechaNacimiento())
                .cuentaBloqueada(request.getCuentaBloqueada())
                .activa(request.getActiva())
                .roles(roles)
                .build();

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public PageResponse<UserResponse> findAll(Pageable pageable) {

        Page<User> page = userRepository.findAll(pageable);

        return PageResponse.<UserResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(userMapper::toResponse)
                                .toList()
                )
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }


    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return userMapper.toResponse(user);
    }


    public UserResponse update(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        List<Role> roles = roleRepository.findAllById(request.getRolesIds());

        if (roles.isEmpty()) {
            throw new IllegalStateException("Debe asignar al menos un rol");
        }

        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setTelefono(request.getTelefono());
        user.setEmail(request.getEmail());
        user.setFechaNacimiento(request.getFechaNacimiento());
        user.setCuentaBloqueada(request.getCuentaBloqueada());
        user.setActiva(request.getActiva());
        user.setRoles(roles);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toResponse(user);
    }


    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        userRepository.delete(user);
    }
}
