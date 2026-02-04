package com.ansicode.SistemaAdministracionGym.banco;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Crear banco", description = "Registra un nuevo banco en el sistema.")
    @ApiResponse(responseCode = "200", description = "Banco creado exitosamente")
    public ResponseEntity<BancoResponse> create(@RequestBody @Valid BancoRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping({ "", "/todos" })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar bancos", description = "Obtiene todos los bancos registrados.")
    @ApiResponse(responseCode = "200", description = "Bancos obtenidos exitosamente")
    public ResponseEntity<List<BancoResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar banco", description = "Actualiza la información de un banco existente.")
    @ApiResponse(responseCode = "200", description = "Banco actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    public ResponseEntity<BancoResponse> update(@PathVariable Long id, @RequestBody @Valid BancoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar banco", description = "Elimina un banco del sistema.")
    @ApiResponse(responseCode = "204", description = "Banco eliminado exitosamente")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
