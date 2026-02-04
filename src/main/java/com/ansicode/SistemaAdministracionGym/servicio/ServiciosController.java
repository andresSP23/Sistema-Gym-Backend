package com.ansicode.SistemaAdministracionGym.servicio;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("servicios")
@RequiredArgsConstructor
@Tag(name = "Servicio")
public class ServiciosController {

    private final ServicioService serviciosService;

    @PostMapping("/crear")
    @Operation(summary = "Crear servicio", description = "Registra un nuevo servicio en el sistema.")
    @ApiResponse(responseCode = "200", description = "Servicio creado exitosamente")
    public ResponseEntity<ServiciosResponse> create(
            @RequestBody @Valid ServiciosRequest request) {
        return ResponseEntity.ok(serviciosService.create(request));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar servicios", description = "Obtiene una lista paginada de servicios.")
    @ApiResponse(responseCode = "200", description = "Servicios obtenidos exitosamente")
    public ResponseEntity<PageResponse<ServiciosResponse>> findAll(
            @RequestParam(name = "suscripcion") Boolean suscripcion,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(serviciosService.list(suscripcion, pageable));
    }

    @GetMapping("/buscar-por-id/{id}")
    @Operation(summary = "Buscar servicio por ID", description = "Obtiene un servicio por su ID único.")
    @ApiResponse(responseCode = "200", description = "Servicio encontrado")
    @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    public ResponseEntity<ServiciosResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(serviciosService.findById(id));
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar servicio", description = "Actualiza un servicio existente.")
    @ApiResponse(responseCode = "200", description = "Servicio actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    public ResponseEntity<ServiciosResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ServiciosRequest request) {
        return ResponseEntity.ok(serviciosService.update(id, request));
    }

    @DeleteMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar servicio", description = "Elimina un servicio del sistema.")
    @ApiResponse(responseCode = "204", description = "Servicio eliminado exitosamente")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        serviciosService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/combo")
    @Operation(summary = "Obtener combo de servicios", description = "Obtiene una lista de servicios para combo/dropdown.")
    @ApiResponse(responseCode = "200", description = "Combo obtenido exitosamente")
    public ResponseEntity<List<ServiciosResponse>> combo(
            @RequestParam(name = "suscripcion") Boolean suscripcion) {
        return ResponseEntity.ok(serviciosService.combo(suscripcion));
    }

}
