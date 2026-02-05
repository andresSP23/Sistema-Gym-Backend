package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller para gestión de movimientos bancarios.
 */
@RestController
@RequestMapping("/bancos/{bancoId}/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos Bancarios", description = "Gestión de movimientos de dinero en bancos")
public class MovimientoBancoController {

    private final MovimientoBancoService movimientoBancoService;

    /**
     * Lista movimientos de un banco con filtros.
     * Orden por defecto: más reciente primero (fecha DESC, id DESC)
     */
    @GetMapping
    public ResponseEntity<PageResponse<MovimientoBancoResponse>> listarMovimientos(
            @PathVariable Long bancoId,
            @RequestParam(required = false) TipoMovimientoBanco tipo,
            @RequestParam(required = false) ConceptoMovimientoBanco concepto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(required = false) String q, // Texto libre para buscar en descripcion/referencia
            Pageable pageable) {

        return ResponseEntity.ok(
                movimientoBancoService.listarMovimientos(bancoId, tipo, concepto, desde, hasta, q, pageable));
    }

    /**
     * Crea un movimiento bancario manual.
     */
    @PostMapping
    public ResponseEntity<MovimientoBancoResponse> crearMovimiento(
            @PathVariable Long bancoId,
            @Valid @RequestBody MovimientoBancoRequest request) {

        return ResponseEntity.ok(movimientoBancoService.crearMovimientoManual(bancoId, request));
    }
}
