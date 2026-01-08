package com.ansicode.SistemaAdministracionGym.asistencia;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("asistencias")
@Tag(name = "Asistencia")
@RequiredArgsConstructor
public class AsistenciaController {


    private final AsistenciaService asistenciaService;

    @PostMapping("/registrar-asistencia")
    public ResponseEntity<AsistenciaResponse> create(@Valid @RequestBody AsistenciaRequest request) {
        AsistenciaResponse response = asistenciaService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<AsistenciaResponse> findById(@PathVariable Long id) {
        AsistenciaResponse response = asistenciaService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> findByCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(asistenciaService.findByCliente(clienteId, pageable));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        asistenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
