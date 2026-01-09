package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ventas")
@Tag(name = "Venta")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping("/nueva-venta")
    public ResponseEntity<VentaResponse> create(
            @RequestBody @Valid VentaRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(
                ventaService.create(request, connectedUser)
        );
    }
}
