package com.ansicode.SistemaAdministracionGym.sesioncaja;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sesion-caja")
@Tag(name ="Sesion Caja")
@RequiredArgsConstructor
public class SesionCajaController {
    private final SesionCajaService service;

    @PostMapping("/abrir")
    public ResponseEntity<SesionCajaResponse> abrir(@Valid @RequestBody AbrirCajaRequest request , Authentication connectedUser) {
        return ResponseEntity.ok(service.abrirCaja(request, connectedUser));
    }

    @PostMapping("/cerrar/{id}")
    public ResponseEntity<SesionCajaResponse> cerrar(@PathVariable Long id, @Valid @RequestBody CerrarCajaRequest request , Authentication connectedUser) {
        return ResponseEntity.ok(service.cerrarCaja(id, request, connectedUser));
    }
}
