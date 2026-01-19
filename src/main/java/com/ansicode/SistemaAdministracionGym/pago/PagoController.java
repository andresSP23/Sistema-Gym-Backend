package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pagos")
@Tag(name = "Pago")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService service;


    @PostMapping
    public PagoResponse registrarPago(@Valid @RequestBody PagoRequest request, Authentication connectedUser) {
        return service.registrarPago(request, connectedUser);
    }
}
