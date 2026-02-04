package com.ansicode.SistemaAdministracionGym.gasto;

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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("gastos")
@RequiredArgsConstructor
@Tag(name = "Gastos")
public class GastoController {

    private final GastoService service;

    @PostMapping("/crear")
    @Operation(summary = "Crear nuevo gasto", description = "Registra un nuevo gasto en el sistema.")
    @ApiResponse(responseCode = "200", description = "Gasto creado exitosamente")
    public ResponseEntity<GastoResponse> create(
            @RequestBody @Valid GastoRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.create(request, connectedUser));
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar gasto", description = "Actualiza un gasto existente por ID.")
    @ApiResponse(responseCode = "200", description = "Gasto actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Gasto no encontrado")
    public ResponseEntity<GastoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid GastoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping
    @Operation(summary = "Buscar gastos", description = "Obtiene una lista paginada de gastos con filtros opcionales.")
    @ApiResponse(responseCode = "200", description = "Gastos obtenidos exitosamente")
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
    @Operation(summary = "Pagar gasto", description = "Marca un gasto como pagado.")
    @ApiResponse(responseCode = "200", description = "Gasto pagado exitosamente")
    @ApiResponse(responseCode = "404", description = "Gasto no encontrado")
    public ResponseEntity<GastoResponse> pagar(
            @PathVariable Long id,
            @RequestBody @Valid PagarGastoRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.pagarGasto(id, request, connectedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar gasto", description = "Elimina un registro de gasto por ID.")
    @ApiResponse(responseCode = "204", description = "Gasto eliminado exitosamente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
