package com.ansicode.SistemaAdministracionGym.conteocaja;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
