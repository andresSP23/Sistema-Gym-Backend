package com.ansicode.SistemaAdministracionGym.servicio;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("servicios")
@RequiredArgsConstructor
@Tag(name = "Servicio")
public class ServiciosController {

    private final ServicioService serviciosService;

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ServiciosResponse> create(
            @RequestBody @Valid ServiciosRequest request) {
        return ResponseEntity.ok(serviciosService.create(request));
    }

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO', 'ENTRENADOR')")
    public ResponseEntity<PageResponse<ServiciosResponse>> findAll(
            @RequestParam(name = "suscripcion") Boolean suscripcion,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(serviciosService.list(suscripcion, pageable));
    }

    @GetMapping("/buscar-por-id/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO', 'ENTRENADOR')")
    public ResponseEntity<ServiciosResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(serviciosService.findById(id));
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ServiciosResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ServiciosRequest request) {
        return ResponseEntity.ok(serviciosService.update(id, request));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        serviciosService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/combo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO', 'ENTRENADOR')")
    public ResponseEntity<List<ServiciosResponse>> combo(
            @RequestParam(name = "suscripcion") Boolean suscripcion) {
        return ResponseEntity.ok(serviciosService.combo(suscripcion));
    }

}
