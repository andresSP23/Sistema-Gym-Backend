package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cuadre-caja")
@RequiredArgsConstructor
@Tag(name = "Cuadre Caja")
public class CuadreCajaController {

    private final  CuadreCajaService cuadreCajaService;

    @PostMapping("/generar/{sesionCajaId}")
    public ResponseEntity<CuadreCaja> generar(
            @PathVariable Long sesionCajaId,
            @RequestBody(required = false) GenerarCuadreRequest body
    ) {
        String moneda = (body != null) ? body.getMoneda() : "USD";
        String obs = (body != null) ? body.getObservacion() : null;

        return ResponseEntity.ok(cuadreCajaService.generarCuadre(sesionCajaId, moneda, obs));
    }
}
