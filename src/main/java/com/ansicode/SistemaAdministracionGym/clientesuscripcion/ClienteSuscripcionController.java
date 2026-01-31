package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping("/cliente-suscripcion/findAll")
    public ResponseEntity<PageResponse<ClienteSuscripcionResponse>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long servicioId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean vigente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                clienteSuscripcionService.listarConFiltros(clienteId, servicioId, estado, vigente, desde, hasta,
                        pageable, mapper));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id,
            @RequestBody(required = false) CancelarSuscripcionRequest request,
            org.springframework.security.core.Authentication connectedUser) {
        clienteSuscripcionService.cancelarSuscripcion(id, request, connectedUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(@PathVariable Long id, @RequestBody EditarSuscripcionRequest request) {
        clienteSuscripcionService.editarSuscripcion(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<com.ansicode.SistemaAdministracionGym.venta.VentaResponse> renovar(
            @PathVariable Long id,
            org.springframework.security.core.Authentication connectedUser) {
        return ResponseEntity.ok(clienteSuscripcionService.renovarSuscripcion(id, connectedUser));
    }
}
