package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @GetMapping("/movimientos/findAll")
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
            Pageable pageable
    ) {
        return service.listar(
                pageable,
                productoId,
                tipoMovimiento,
                desde,
                hasta,
                createdBy,
                cantidadMin,
                cantidadMax,
                stockActualMin,
                stockActualMax,
                q
        );
    }
}
