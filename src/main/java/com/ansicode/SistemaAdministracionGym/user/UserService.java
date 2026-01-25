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
    public UserResponse create(UserRequest request, Authentication connectedUser) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("El email ya está registrado");
        }

        List<Role> roles = roleRepository.findAllById(request.getRolesIds());
        if (roles.isEmpty()) {
            throw new IllegalStateException("Debe asignar al menos un rol");
        }

        // Mapper (password aún sin encriptar)
        User user = userMapper.toEntity(request, roles);

        // Encriptación en service
        user.setPassword(passwordEncoder.encode(request.getPassword()));

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

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request, Authentication connectedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // -------------------------
        // 1) Normalizar request para update parcial:
        // si viene null, conserva el valor actual
        // -------------------------
        if (request.getNombre() == null) {
            request.setNombre(user.getNombre());
        }
        if (request.getApellido() == null) {
            request.setApellido(user.getApellido());
        }
        if (request.getTelefono() == null) {
            request.setTelefono(user.getTelefono());
        }
        if (request.getFechaNacimiento() == null) {
            request.setFechaNacimiento(user.getFechaNacimiento());
        }

        // -------------------------
        // 2) Mapper (solo perfil)
        // -------------------------
        userMapper.mapProfileForUpdate(user, request);

        // -------------------------
        // 3) Password (opcional)
        // -------------------------
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // -------------------------
        // 4) Roles (opcional, normalmente solo admin)
        // -------------------------
        if (request.getRolesIds() != null) {

            List<Role> roles = roleRepository.findAllById(request.getRolesIds());

            if (roles.isEmpty()) {
                throw new IllegalStateException("Debe asignar al menos un rol");
            }

            user.setRoles(roles);
        }

        // -------------------------
        // 5) Persistir y responder
        // -------------------------
        userRepository.save(user);

        return userMapper.toResponse(user);
    }



    @Transactional
    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        userRepository.delete(user);
    }





    public UserResponse getCurrentUser(Authentication authentication) {

        User user;

        if (authentication.getPrincipal() instanceof User u) {
            user = u;
        } else {
            String email = authentication.getName();
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        }

        return userMapper.toResponse(user);
    }
}
