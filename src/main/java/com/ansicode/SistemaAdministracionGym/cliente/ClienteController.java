package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.pago.PagoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clientes")
@Tag(name = "Cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ClienteResponse create(
            @Valid @RequestBody ClienteRequest request) {
        return clienteService.create(request);
    }

    // @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/listar")
    public ResponseEntity<PageResponse<ClienteResponse>> findAll(

            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(clienteService.findAll(pageable));
    }

    @GetMapping("/buscar-por-id/{id}")
    // PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ClienteResponse findById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    @GetMapping("/by-cedula/{cedula}")
    // @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ClienteResponse findByCedula(@PathVariable String cedula) {
        return clienteService.findByCedula(cedula);
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ClienteResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return clienteService.update(id, request);
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void delete(@PathVariable Long id) {
        clienteService.delete(id);
    }
}
