package com.ansicode.SistemaAdministracionGym.sucursal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sucursal")
@Tag(name = "Sucursal")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping("/registrar")
    @Operation(summary = "Registrar sucursal", description = "Registra una nueva sucursal/sede del gimnasio.")
    @ApiResponse(responseCode = "200", description = "Sucursal registrada exitosamente")
    public ResponseEntity<SucursalResponse> registrar(
            @Valid @RequestBody SucursalRequest request) {
        return ResponseEntity.ok(sucursalService.registrar(request));
    }

    @GetMapping("/obtener")
    @Operation(summary = "Obtener sucursal", description = "Obtiene la información de la sucursal del gimnasio.")
    @ApiResponse(responseCode = "200", description = "Sucursal obtenida exitosamente")
    public ResponseEntity<SucursalResponse> obtener() {
        return ResponseEntity.ok(sucursalService.obtener());
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Actualizar sucursal", description = "Actualiza la información de la sucursal del gimnasio.")
    @ApiResponse(responseCode = "200", description = "Sucursal actualizada exitosamente")
    public ResponseEntity<SucursalResponse> actualizar(
            @Valid @RequestBody SucursalRequest request) {
        return ResponseEntity.ok(sucursalService.actualizar(request));
    }
}
