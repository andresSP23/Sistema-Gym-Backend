package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("membresias-clientes")
@Tag(name = "Membresias Clientes")
public class MembresiaClienteController {

    private final MembresiaClienteService service;

    @PostMapping("/create")
    public ResponseEntity<MembresiaClienteResponse> create(
            @Valid @RequestBody MembresiaClienteRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<MembresiaClienteResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<PageResponse<MembresiaClienteResponse>> findByEstado(
            @PathVariable EstadoMembresia estado,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findByEstado(estado, pageable));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<MembresiaClienteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MembresiaClienteResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MembresiaClienteRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
