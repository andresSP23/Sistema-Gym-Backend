package com.ansicode.SistemaAdministracionGym.producto;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("productos")
@Tag( name = "Producto")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductoResponse> create(@RequestBody ProductoRequest request) {
        ProductoResponse response = service.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("findAll")
    public ResponseEntity<?> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/stock")
    public ResponseEntity<?> findByStockGreaterThan(
            @RequestParam Integer stock,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findByStockGreaterThan(stock, pageable));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Long id) {
        ProductoResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductoResponse> update(
            @PathVariable Long id,
            @RequestBody ProductoRequest request
    ) {
        ProductoResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
