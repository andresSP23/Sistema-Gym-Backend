package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("equipamientos")
@Tag(name = "Equipamientos")
@RequiredArgsConstructor
public class EquipamientoController {

    private final EquipamientoService equipamientoService;

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear equipamiento", description = "Registra un nuevo equipamiento del gimnasio.")
    @ApiResponse(responseCode = "200", description = "Equipamiento creado exitosamente")
    public ResponseEntity<EquipamientoResponse> create(
            @RequestBody @Valid EquipamientoRequest request,
            org.springframework.security.core.Authentication connectedUser) {
        return ResponseEntity.ok(equipamientoService.create(request, connectedUser));
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar equipamiento", description = "Actualiza la información de un equipamiento existente.")
    @ApiResponse(responseCode = "200", description = "Equipamiento actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Equipamiento no encontrado")
    public ResponseEntity<EquipamientoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid EquipamientoRequest request) {
        return ResponseEntity.ok(equipamientoService.update(id, request));
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTRENADOR')")
    @Operation(summary = "Buscar equipamiento por ID", description = "Obtiene los detalles de un equipamiento por ID.")
    @ApiResponse(responseCode = "200", description = "Equipamiento encontrado")
    @ApiResponse(responseCode = "404", description = "Equipamiento no encontrado")
    public ResponseEntity<EquipamientoResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.findById(id));
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTRENADOR')")
    @Operation(summary = "Listar equipamientos", description = "Obtiene una lista paginada de todos los equipamientos del gimnasio.")
    @ApiResponse(responseCode = "200", description = "Lista de equipamientos obtenida exitosamente")
    public ResponseEntity<PageResponse<EquipamientoResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(equipamientoService.findAll(pageable));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar equipamiento", description = "Elimina un equipamiento del sistema.")
    @ApiResponse(responseCode = "204", description = "Equipamiento eliminado exitosamente")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        equipamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
