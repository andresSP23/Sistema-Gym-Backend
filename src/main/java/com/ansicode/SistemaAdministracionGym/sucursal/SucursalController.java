package com.ansicode.SistemaAdministracionGym.sucursal;

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
    public ResponseEntity<SucursalResponse> registrar(
            @Valid @RequestBody SucursalRequest request
    ) {
        return ResponseEntity.ok(sucursalService.registrar(request));
    }

    @GetMapping("/obtener")
    public ResponseEntity<SucursalResponse> obtener() {
        return ResponseEntity.ok(sucursalService.obtener());
    }


    @PutMapping("/actualizar")
    public ResponseEntity<SucursalResponse> actualizar(
            @Valid @RequestBody SucursalRequest request
    ) {
        return ResponseEntity.ok(sucursalService.actualizar(request));
    }
}
