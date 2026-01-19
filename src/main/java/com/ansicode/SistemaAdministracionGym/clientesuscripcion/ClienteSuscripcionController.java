package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("suscripciones")
@RequiredArgsConstructor
@Tag(name = "Suscripcion")
public class ClienteSuscripcionController {

    private final ClienteSuscripcionService clienteSuscripcionService;
    private final ClienteSuscripcionMapper mapper;

    @GetMapping("/activa/{clienteId}")
    public ResponseEntity<ClienteSuscripcionResponse> obtenerActiva(@PathVariable Long clienteId) {
        ClienteSuscripcionResponse res = clienteSuscripcionService.obtenerSuscripcionActiva(clienteId, mapper);
        return ResponseEntity.ok(res);
    }
}
