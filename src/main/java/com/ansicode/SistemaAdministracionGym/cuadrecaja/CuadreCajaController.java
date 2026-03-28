package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cuadre-caja")
@RequiredArgsConstructor
@Tag(name = "Cuadre Caja")
public class CuadreCajaController {

    private final CuadreCajaService cuadreCajaService;

    @PostMapping("/generar/{sesionCajaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO')")
    public ResponseEntity<CuadreCajaResponse> generar(
            @PathVariable Long sesionCajaId,
            @RequestBody(required = false) GenerarCuadreRequest body
    ) {
        return ResponseEntity.ok(cuadreCajaService.generarCuadre(sesionCajaId, body));
    }



    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO')")
    public ResponseEntity<PageResponse<CuadreCajaResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(cuadreCajaService.findAll(pageable));
    }


}