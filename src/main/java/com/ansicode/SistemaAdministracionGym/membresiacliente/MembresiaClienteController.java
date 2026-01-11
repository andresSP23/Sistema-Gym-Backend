package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.pago.PagoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("membresias-clientes")
@Tag(name = "Membresias Clientes")
public class MembresiaClienteController {


    private final MembresiaClienteService service;


    @PostMapping("/crear-asignacion")
    @ResponseStatus(HttpStatus.CREATED)
    public MembresiaClienteResponse create(
            @Valid @RequestBody MembresiaClienteRequest request
    ) {
        return service.create(request);
    }


    @PutMapping("/update/{id}")
    public MembresiaClienteResponse update(
            @PathVariable Long id,
            @Valid @RequestBody MembresiaClienteRequest request
    ) {
        return service.update(id, request);
    }


    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<MembresiaClienteResponse>> findAll(

            @RequestParam(name = "page", defaultValue = "0" ,required = false) int page,
            @RequestParam(name = "size", defaultValue = "10" ,required = false) int size,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }


    @GetMapping("/estado/{estado}")
    public PageResponse<MembresiaClienteResponse> findByEstado(
            @PathVariable EstadoMembresia estado,
            Pageable pageable
    ) {
        return service.findByEstado(estado, pageable);
    }


    @GetMapping("/cliente/{clienteId}/activa")
    public MembresiaCliente obtenerMembresiaActiva(
            @PathVariable Long clienteId
    ) {
        return service.obtenerMembresiaActivaPorCliente(clienteId);
    }

}
