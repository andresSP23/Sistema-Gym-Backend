package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("movimiento-inventario")
@Tag(name = "Movimiento Inventario")
@RequiredArgsConstructor
public class MovimientoInventarioController {

        private final MovimientoInventarioService service;

        /**
         * Lista movimientos de inventario con filtros y orden DESC por defecto.
         */
        @GetMapping("/movimientos/findAll")
        @Operation(summary = "Listar movimientos", description = "Lista movimientos de inventario con filtros. Orden por defecto: más reciente primero.")
        @ApiResponse(responseCode = "200", description = "Movimientos obtenidos exitosamente")
        public PageResponse<MovimientoInventarioResponse> listar(
                        @RequestParam(required = false) Long productoId,
                        @RequestParam(required = false) String tipoMovimiento,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
                        @RequestParam(required = false) Long createdBy,
                        @RequestParam(required = false) Integer cantidadMin,
                        @RequestParam(required = false) Integer cantidadMax,
                        @RequestParam(required = false) Integer stockActualMin,
                        @RequestParam(required = false) Integer stockActualMax,
                        @RequestParam(required = false) String q,
                        Pageable pageable) {
                // Aplicar orden DESC por defecto si no se especifica
                Sort effectiveSort = pageable.getSort().isSorted()
                                ? pageable.getSort()
                                : Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"));

                Pageable effectivePageable = PageRequest.of(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                effectiveSort);

                return service.listar(
                                effectivePageable,
                                productoId,
                                tipoMovimiento,
                                desde,
                                hasta,
                                createdBy,
                                cantidadMin,
                                cantidadMax,
                                stockActualMin,
                                stockActualMax,
                                q);
        }
}
