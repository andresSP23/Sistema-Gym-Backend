package com.ansicode.SistemaAdministracionGym.banco;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("bancos")
@Tag(name = "Bancos")
@RequiredArgsConstructor
public class BancoController {

    private final BancoService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<BancoResponse> create(@RequestBody @Valid BancoRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping({ "", "/todos" })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<BancoResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<BancoResponse> update(@PathVariable Long id, @RequestBody @Valid BancoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
