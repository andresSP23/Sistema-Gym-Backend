package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Crear venta de servicio", description = "Crea una nueva venta de un servicio.")
    @ApiResponse(responseCode = "200", description = "Venta de servicio creada exitosamente")
    public ResponseEntity<VentaResponse> crearVentaServicio(
            @RequestBody @Valid CrearVentaServicioRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(ventaService.crearVentaServicio(request, connectedUser));
    }

    @PostMapping("/crear-venta-productos")
    @Operation(summary = "Crear venta de productos", description = "Crea una nueva venta de productos.")
    @ApiResponse(responseCode = "200", description = "Venta de productos creada exitosamente")
    public ResponseEntity<VentaResponse> crearVentaProductos(
            @RequestBody @Valid CrearVentaProductoRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(ventaService.crearVentaProductos(request, connectedUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venta por ID", description = "Obtiene una venta por su ID único.")
    @ApiResponse(responseCode = "200", description = "Venta encontrada")
    @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    public ResponseEntity<VentaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.findById(id));
    }
}
