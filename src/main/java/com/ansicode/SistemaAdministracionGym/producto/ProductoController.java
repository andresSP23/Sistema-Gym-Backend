package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("productos")
@Tag(name = "Producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping("/crear-producto")
    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en el inventario.")
    @ApiResponse(responseCode = "200", description = "Producto creado exitosamente")
    public ResponseEntity<ProductoResponse> create(
            @RequestBody @Valid ProductoRequest request) {
        return ResponseEntity.ok(productoService.create(request));
    }

    @GetMapping("/findAll")
    @Operation(summary = "Listar productos", description = "Obtiene una lista paginada de todos los productos.")
    @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente")
    public ResponseEntity<PageResponse<ProductoResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable

    ) {
        return ResponseEntity.ok(productoService.findAll(pageable));
    }

    @GetMapping("findById/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Obtiene un producto por su ID único.")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<ProductoResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente.")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<ProductoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ProductoRequest request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del inventario.")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/agregar-stock")
    @Operation(summary = "Agregar stock", description = "Agrega stock a un producto existente.")
    @ApiResponse(responseCode = "200", description = "Stock agregado exitosamente")
    public ResponseEntity<Void> agregarStock(
            @PathVariable Long id,
            @Valid @RequestBody AgregarStockRequest request, Authentication connectedUser) {
        productoService.agregarStock(id, request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/ajustar-stock")
    @Operation(summary = "Ajustar stock", description = "Ajusta la cantidad de stock de un producto.")
    @ApiResponse(responseCode = "200", description = "Stock ajustado exitosamente")
    public ResponseEntity<Void> ajustarStock(
            @PathVariable Long id,
            @Valid @RequestBody AjustarStockRequest request) {
        productoService.ajustarStock(id, request);
        return ResponseEntity.ok().build();
    }
}
