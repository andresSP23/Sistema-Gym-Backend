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
    @PostMapping("/crear-venta-servicio")
    public ResponseEntity<VentaResponse> crearVentaServicio(
            @RequestBody @Valid CrearVentaServicioRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(ventaService.crearVentaServicio(request, connectedUser));
    }


    @PostMapping("/crear-venta-productos")
    public ResponseEntity<VentaResponse> crearVentaProductos(
            @RequestBody @Valid CrearVentaProductoRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(ventaService.crearVentaProductos(request, connectedUser));
    }
}
