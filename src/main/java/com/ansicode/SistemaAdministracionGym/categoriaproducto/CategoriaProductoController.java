package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categorias-producto")
@Tag(name = "Categoria Producto")
@RequiredArgsConstructor
public class CategoriaProductoController {
        private final CategoriaProductoService categoriaProductoService;

        // ===================== CREATE =====================
        @PostMapping("/crear")
        public ResponseEntity<CategoriaProductoResponse> create(
                        @RequestBody @Valid CategoriaProductoRequest request) {
                return ResponseEntity.ok(
                                categoriaProductoService.create(request));
        }

        // ===================== FIND ALL (PAGE) =====================
        @GetMapping("/listar")
        public ResponseEntity<PageResponse<CategoriaProductoResponse>> findAll(
                        Pageable pageable) {
                return ResponseEntity.ok(
                                categoriaProductoService.findAll(pageable));
        }

        // ===================== FIND BY ID =====================
        @GetMapping("/buscar-por-id/{id}")
        public ResponseEntity<CategoriaProductoResponse> findById(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                categoriaProductoService.findById(id));
        }

        // ===================== UPDATE =====================
        @PutMapping("/actualizar/{id}")
        public ResponseEntity<CategoriaProductoResponse> update(
                        @PathVariable Long id,
                        @RequestBody @Valid CategoriaProductoRequest request) {
                return ResponseEntity.ok(
                                categoriaProductoService.update(id, request));
        }

        // ===================== DELETE (LOGICAL) =====================
        @DeleteMapping("/eliminar/{id}")
        public ResponseEntity<Void> delete(
                        @PathVariable Long id) {
                categoriaProductoService.delete(id);
                return ResponseEntity.noContent().build();
        }
}
