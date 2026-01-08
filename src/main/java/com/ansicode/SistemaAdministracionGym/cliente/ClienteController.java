package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clientes")
@Tag(name = "Cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/create")
    //@PreAuthorize("hasRole('ADMIN')")
    public ClienteResponse create(
            @Valid @RequestBody ClienteRequest request
    ) {
        return clienteService.create(request);
    }

    @GetMapping("/findAll")
   // @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public PageResponse<ClienteResponse> findAll(Pageable pageable) {
        return clienteService.findAll(pageable);
    }

    @GetMapping("/findById/{id}")
    //PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ClienteResponse findById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    @GetMapping("/cedula/{cedula}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ClienteResponse findByCedula(@PathVariable String cedula) {
        return clienteService.findByCedula(cedula);
    }

    @PutMapping("/update/{id}")
   // @PreAuthorize("hasRole('ADMIN')")
    public ClienteResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request
    ) {
        return clienteService.update(id, request);
    }

    @DeleteMapping("/delete/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        clienteService.delete(id);
    }
}
