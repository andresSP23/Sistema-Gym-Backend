package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
        @Operation(summary = "Crear categoría de producto", description = "Registra una nueva categoría de producto.")
        @ApiResponse(responseCode = "200", description = "Categoría creada exitosamente")
        public ResponseEntity<CategoriaProductoResponse> create(
                        @RequestBody @Valid CategoriaProductoRequest request) {
                return ResponseEntity.ok(
                                categoriaProductoService.create(request));
        }

        // ===================== FIND ALL (PAGE) =====================
        @GetMapping("/findAll")
        @Operation(summary = "Listar categorías de productos", description = "Obtiene una lista paginada de categorías de productos.")
        @ApiResponse(responseCode = "200", description = "Categorías obtenidas exitosamente")
        public ResponseEntity<PageResponse<CategoriaProductoResponse>> findAll(
                        Pageable pageable) {
                return ResponseEntity.ok(
                                categoriaProductoService.findAll(pageable));
        }

        // ===================== FIND BY ID =====================
        @GetMapping("/findById/{id}")
        @Operation(summary = "Buscar categoría por ID", description = "Obtiene una categoría de producto por su ID único.")
        @ApiResponse(responseCode = "200", description = "Categoría encontrada")
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
        public ResponseEntity<CategoriaProductoResponse> findById(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                categoriaProductoService.findById(id));
        }

        // ===================== UPDATE =====================
        @PutMapping("/update/{id}")
        @Operation(summary = "Actualizar categoría de producto", description = "Actualiza una categoría de producto existente.")
        @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente")
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
        public ResponseEntity<CategoriaProductoResponse> update(
                        @PathVariable Long id,
                        @RequestBody @Valid CategoriaProductoRequest request) {
                return ResponseEntity.ok(
                                categoriaProductoService.update(id, request));
        }

        // ===================== DELETE (LOGICAL) =====================
        @DeleteMapping("/delete/{id}")
        @Operation(summary = "Eliminar categoría de producto", description = "Elimina lógicamente una categoría de producto.")
        @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente")
        public ResponseEntity<Void> delete(
                        @PathVariable Long id) {
                categoriaProductoService.delete(id);
                return ResponseEntity.noContent().build();
        }
}
