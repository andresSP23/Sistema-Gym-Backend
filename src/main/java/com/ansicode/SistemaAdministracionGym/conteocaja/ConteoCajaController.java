package com.ansicode.SistemaAdministracionGym.conteocaja;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("conteo-caja")
@RequiredArgsConstructor
@Tag(name = "Conteo Caja")
public class ConteoCajaController {

    private final ConteoCajaService conteoCajaService;

    @PostMapping("/guardar")
    public ResponseEntity<Void> guardar(@RequestBody @Valid GuardarConteoCajaRequest request) {
        conteoCajaService.guardarConteo(request);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/sesion/{sesionCajaId}")
    public ResponseEntity<ConteoCajaResponse> obtenerPorSesion(@PathVariable Long sesionCajaId) {
        return ResponseEntity.ok(conteoCajaService.obtenerPorSesion(sesionCajaId));
    }

}
