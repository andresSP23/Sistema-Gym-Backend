package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;

@RestController
@RequestMapping("reportes")
@RequiredArgsConstructor
@Tag(name = "Reporte Pagos")
public class ReportePagoController {

    private final ReportePagoService reportePagoExcelService;

    @GetMapping("/excel")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Resource> exportarExcel(

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,

            @RequestParam(required = false) TipoOperacionPago tipoOperacion,

            @RequestParam(required = false) MetodoPago metodo,

            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoPago estado,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nombre) {
        return reportePagoExcelService.exportar(desde, hasta, tipoOperacion, metodo, clienteId, estado, documento,
                nombre);
    }
}
