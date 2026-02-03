package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;

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
    public ResponseEntity<EquipamientoResponse> create(
            @RequestBody @Valid EquipamientoRequest request,
            org.springframework.security.core.Authentication connectedUser) {
        return ResponseEntity.ok(equipamientoService.create(request, connectedUser));
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<EquipamientoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid EquipamientoRequest request) {
        return ResponseEntity.ok(equipamientoService.update(id, request));
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTRENADOR')")
    public ResponseEntity<EquipamientoResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.findById(id));
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTRENADOR')")
    public ResponseEntity<PageResponse<EquipamientoResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(equipamientoService.findAll(pageable));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        equipamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
