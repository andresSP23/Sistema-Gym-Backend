package com.ansicode.SistemaAdministracionGym.mantenimiento;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("mantenimientos")
@Tag(name = "Mantenimiento Equipos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MantenimientoResponse> create(@RequestBody @Valid MantenimientoRequest request,
            org.springframework.security.core.Authentication connectedUser) {
        return ResponseEntity.ok(service.create(request, connectedUser));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PageResponse<MantenimientoResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaRealizacion"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/equipamiento/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<MantenimientoResponse>> findByEquipamiento(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByEquipamiento(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
