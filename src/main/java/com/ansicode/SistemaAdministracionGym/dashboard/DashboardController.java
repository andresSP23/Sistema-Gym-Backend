package com.ansicode.SistemaAdministracionGym.dashboard;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("dashboard")
@RequiredArgsConstructor
@Tag(name ="DashBoard")
public class DashboardController {


    private final DashboardService dashboardService;

    @GetMapping("/obtenerDashboard")
    public DashBoardResumenResponse obtenerDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin
    ) {
        return dashboardService.obtenerResumen(fechaInicio, fechaFin);
    }
}
