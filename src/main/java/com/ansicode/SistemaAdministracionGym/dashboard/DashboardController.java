package com.ansicode.SistemaAdministracionGym.dashboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("dashboard")
@RequiredArgsConstructor
@Tag(name = "DashBoard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumen")
    @Operation(summary = "Resumen del dashboard", description = "Obtiene métricas clave y estadísticas resumidas para el dashboard.")
    @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente")
    public ResponseEntity<DashBoardResumenResponse> resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(dashboardService.resumen(desde, hasta));
    }

    @GetMapping("/ingresos-diarios")
    @Operation(summary = "Ingresos diarios", description = "Obtiene datos de ingresos diarios para gráficos.")
    @ApiResponse(responseCode = "200", description = "Datos de ingresos obtenidos exitosamente")
    public ResponseEntity<List<SerieResponse>> ingresosDiarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(dashboardService.ingresosDiarios(desde, hasta));
    }

    @GetMapping("/egresos-diarios")
    @Operation(summary = "Egresos diarios", description = "Obtiene datos de egresos diarios para gráficos.")
    @ApiResponse(responseCode = "200", description = "Datos de egresos obtenidos exitosamente")
    public ResponseEntity<List<SerieResponse>> egresosDiarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(dashboardService.egresosDiarios(desde, hasta));
    }

    @GetMapping("/top-vendidos")
    @Operation(summary = "Artículos más vendidos", description = "Obtiene los productos o servicios más vendidos.")
    @ApiResponse(responseCode = "200", description = "Artículos más vendidos obtenidos exitosamente")
    public ResponseEntity<List<TopVendidoResponse>> topVendidos(
            @RequestParam String tipo, // PRODUCTO | SERVICIO

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,

            @RequestParam(defaultValue = "10", required = false) int limit) {
        return ResponseEntity.ok(dashboardService.topVendidos(tipo, desde, hasta, limit));
    }

}
