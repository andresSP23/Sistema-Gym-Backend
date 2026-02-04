package com.ansicode.SistemaAdministracionGym.asistencia;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("asistencias")
@Tag(name = "Asistencia")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    // 1) Registrar asistencia por cédula
    @PostMapping("/registrar-por-cedula")
    @Operation(summary = "Registrar asistencia", description = "Registra la asistencia de un cliente al gimnasio por número de cédula.")
    @ApiResponse(responseCode = "200", description = "Asistencia registrada exitosamente")
    public ResponseEntity<AsistenciaResponse> registrarPorCedula(
            @RequestBody @Valid AsistenciaRequest request) {
        return ResponseEntity.ok(asistenciaService.registrarPorCedula(request));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar asistencias del cliente", description = "Obtiene el historial de asistencias de un cliente específico.")
    @ApiResponse(responseCode = "200", description = "Lista de asistencias obtenida exitosamente")
    public ResponseEntity<PageResponse<AsistenciaResponse>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaEntrada,desc") String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(asistenciaService.listarPorCliente(clienteId, pageable));
    }

    private Pageable buildPageable(int page, int size, String sort) {
        // sort = "campo,asc" o "campo,desc"
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, field));
    }
}
