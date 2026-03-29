package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categorias-producto")
@Tag(name = "Categoria Producto")
@RequiredArgsConstructor
public class CategoriaProductoController {
        private final CategoriaProductoService categoriaProductoService;

        // ===================== CREATE =====================
        @PostMapping("/crear")
        @PreAuthorize("hasRole('ADMINISTRADOR')")
        public ResponseEntity<CategoriaProductoResponse> create(
                        @RequestBody @Valid CategoriaProductoRequest request) {
                return ResponseEntity.ok(
                                categoriaProductoService.create(request));
        }

        // ===================== FIND ALL (PAGE) =====================
        @GetMapping("/listar")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO')")
        public ResponseEntity<PageResponse<CategoriaProductoResponse>> findAll(
                        Pageable pageable) {
                return ResponseEntity.ok(
                                categoriaProductoService.findAll(pageable));
        }

        // ===================== FIND BY ID =====================
        @GetMapping("/buscar-por-id/{id}")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CAJERO')")
        public ResponseEntity<CategoriaProductoResponse> findById(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                categoriaProductoService.findById(id));
        }

        // ===================== UPDATE =====================
        @PutMapping("/actualizar/{id}")
        @PreAuthorize("hasRole('ADMINISTRADOR')")
        public ResponseEntity<CategoriaProductoResponse> update(
                        @PathVariable Long id,
                        @RequestBody @Valid CategoriaProductoRequest request) {
                return ResponseEntity.ok(
                                categoriaProductoService.update(id, request));
        }

        // ===================== DELETE (LOGICAL) =====================
        @DeleteMapping("/eliminar/{id}")
        @PreAuthorize("hasRole('ADMINISTRADOR')")
        public ResponseEntity<Void> delete(
                        @PathVariable Long id) {
                categoriaProductoService.delete(id);
                return ResponseEntity.noContent().build();
        }
}
