package com.ansicode.SistemaAdministracionGym.sesioncaja;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sesion-caja")
@Tag(name ="Sesion Caja")
@RequiredArgsConstructor
public class SesionCajaController {
    private final SesionCajaService service;

    @PostMapping("/abrir")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO')")
    public ResponseEntity<SesionCajaResponse> abrir(@Valid @RequestBody AbrirCajaRequest request , Authentication connectedUser) {
        return ResponseEntity.ok(service.abrirCaja(request, connectedUser));
    }

    @PostMapping("/cerrar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO')")
    public ResponseEntity<SesionCajaResponse> cerrar(@PathVariable Long id, @Valid @RequestBody CerrarCajaRequest request , Authentication connectedUser) {
        return ResponseEntity.ok(service.cerrarCaja(id, request, connectedUser));
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO')")
    public ResponseEntity<PageResponse<SesionCajaResponse>> findAll(

            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(pageable));
    }
}
