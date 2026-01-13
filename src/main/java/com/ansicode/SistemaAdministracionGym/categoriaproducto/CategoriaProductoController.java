package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categoria-producto")
@Tag(name = "Categoria Producto")
@RequiredArgsConstructor
public class CategoriaProductoController {
    private final CategoriaProductoService categoriaProductoService;

    // ===================== CREATE =====================
    @PostMapping("/crear-categoria")
    public ResponseEntity<CategoriaProductoResponse> create(
            @RequestBody @Valid CategoriaProductoRequest request
    ) {
        return ResponseEntity.ok(
                categoriaProductoService.create(request)
        );
    }

    // ===================== FIND ALL (PAGE) =====================
    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<CategoriaProductoResponse>> findAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                categoriaProductoService.findAll(pageable)
        );
    }

    // ===================== FIND BY ID =====================
    @GetMapping("/findById/{id}")
    public ResponseEntity<CategoriaProductoResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                categoriaProductoService.findById(id)
        );
    }

    // ===================== UPDATE =====================
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoriaProductoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaProductoRequest request
    ) {
        return ResponseEntity.ok(
                categoriaProductoService.update(id, request)
        );
    }

    // ===================== DELETE (LOGICAL) =====================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        categoriaProductoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
