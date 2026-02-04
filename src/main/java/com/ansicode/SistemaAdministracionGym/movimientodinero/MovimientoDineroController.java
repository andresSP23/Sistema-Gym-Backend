package com.ansicode.SistemaAdministracionGym.movimientodinero;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("movimientos-dinero")
@RequiredArgsConstructor
@Tag(name = "Movimiento Dinero")
public class MovimientoDineroController {

    private final MovimientoDineroService movimientoDineroService;

    @GetMapping("/listar")
    @Operation(summary = "Listar movimientos de dinero", description = "Obtiene una lista paginada de movimientos de dinero con filtros opcionales.")
    @ApiResponse(responseCode = "200", description = "Movimientos de dinero obtenidos exitosamente")
    public PageResponse<MovimientoDineroResponse> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            // Orden seguro
            @RequestParam(defaultValue = "fecha") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,

            // Filtros opcionales
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String concepto,
            @RequestParam(required = false) String metodo,
            @RequestParam(required = false) String moneda,
            @RequestParam(required = false) Long usuarioId,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        // Whitelist para evitar PropertyReferenceException
        String sortField = switch (sortBy) {
            case "fecha" -> "fecha";
            case "monto" -> "monto";
            case "tipo" -> "tipo";
            case "concepto" -> "concepto";
            case "id" -> "id";
            default -> "fecha";
        };

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return movimientoDineroService.listarTodos(tipo, concepto, metodo, moneda, usuarioId, desde, hasta, pageable);
    }
}
