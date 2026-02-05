package com.ansicode.SistemaAdministracionGym.gasto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("gastos")
@RequiredArgsConstructor
@Tag(name = "Gastos")
public class GastoController {

    private final GastoService service;

    @PostMapping("/crear")
    public ResponseEntity<GastoResponse> create(
            @RequestBody @Valid GastoRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.create(request, connectedUser));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GastoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid GastoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/listar")
    public ResponseEntity<PageResponse<GastoResponse>> findAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) java.time.LocalDate desde,
            @RequestParam(required = false) java.time.LocalDate hasta,
            @RequestParam(required = false) com.ansicode.SistemaAdministracionGym.enums.EstadoGasto estado,
            @RequestParam(required = false) com.ansicode.SistemaAdministracionGym.enums.CategoriaGasto categoria,
            @RequestParam(required = false) com.ansicode.SistemaAdministracionGym.enums.MetodoPago metodo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Orden por defecto: fechaGasto descendente
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaGasto").descending());
        return ResponseEntity.ok(service.findAll(nombre, desde, hasta, estado, categoria, metodo, pageable));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<GastoResponse> pagar(
            @PathVariable Long id,
            @RequestBody @Valid PagarGastoRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.pagarGasto(id, request, connectedUser));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
