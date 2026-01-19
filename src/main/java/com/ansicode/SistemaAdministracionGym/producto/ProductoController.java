package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
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
@Tag( name = "Producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping("/crear-producto")
    public ResponseEntity<ProductoResponse> create(
            @RequestBody @Valid ProductoRequest request
    ) {
        return ResponseEntity.ok(productoService.create(request));
    }


    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<ProductoResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "0" ,required = false) int page,
            @RequestParam(name = "size", defaultValue = "10" ,required = false) int size,
            @ParameterObject Pageable pageable

    ) {
        return ResponseEntity.ok(productoService.findAll(pageable));
    }

    @GetMapping("findById/{id}")
    public ResponseEntity<ProductoResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ProductoRequest request
    ) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/agregar-stock")
    public ResponseEntity<Void> agregarStock(
            @PathVariable Long id,
            @Valid @RequestBody AgregarStockRequest request , Authentication connectedUser
    ) {
        productoService.agregarStock(id, request ,  connectedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/ajustar-stock")
    public ResponseEntity<Void> ajustarStock(
            @PathVariable Long id,
            @Valid @RequestBody AjustarStockRequest request
    ) {
        productoService.ajustarStock(id, request);
        return ResponseEntity.ok().build();
    }
}
