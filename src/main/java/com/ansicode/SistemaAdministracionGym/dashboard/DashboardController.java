package com.ansicode.SistemaAdministracionGym.dashboard;

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
    public ResponseEntity<DashBoardResumenResponse> resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(dashboardService.resumen(desde, hasta));
    }

    @GetMapping("/ingresos-diarios")
    public ResponseEntity<List<SerieResponse>> ingresosDiarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(dashboardService.ingresosDiarios(desde, hasta));
    }

    @GetMapping("/egresos-diarios")
    public ResponseEntity<List<SerieResponse>> egresosDiarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(dashboardService.egresosDiarios(desde, hasta));
    }

    @GetMapping("/top-vendidos")
    public ResponseEntity<List<TopVendidoResponse>> topVendidos(
            @RequestParam String tipo, // PRODUCTO | SERVICIO

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,

            @RequestParam(defaultValue = "10", required = false) int limit) {
        return ResponseEntity.ok(dashboardService.topVendidos(tipo, desde, hasta, limit));
    }

    @GetMapping("/ultimas-asistencias")
    public ResponseEntity<List<DashboardAsistenciaResponse>> ultimasAsistencias() {
        return ResponseEntity.ok(dashboardService.ultimasAsistencias());
    }

}
