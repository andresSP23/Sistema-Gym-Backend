package com.ansicode.SistemaAdministracionGym.user;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.handler.ApiErrorResponses;
import com.ansicode.SistemaAdministracionGym.pago.PagoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("users")
@Tag(name = "User")
@RequiredArgsConstructor
@ApiErrorResponses
public class UserController {
    private final UserService userService;

    @PostMapping("/crear")
    @ResponseStatus(HttpStatus.CREATED)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario del sistema.")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    public UserResponse create(@RequestBody @Valid UserRequest request, Authentication connectedUser) {
        return userService.create(request, connectedUser);
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar usuarios", description = "Obtiene una lista paginada de todos los usuarios.")
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente")
    public ResponseEntity<PageResponse<UserResponse>> findAll(

            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/buscar-por-id/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Obtiene un usuario por su ID único.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PatchMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza la información de un usuario existente.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        UserResponse response = userService.update(id, request, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Obtiene la información del usuario autenticado actualmente.")
    @ApiResponse(responseCode = "200", description = "Usuario actual obtenido exitosamente")
    public UserResponse me(Authentication authentication) {
        return userService.getCurrentUser(authentication);
    }
}
