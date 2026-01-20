package com.ansicode.SistemaAdministracionGym.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {


    private final MovimientoDineroRepositoryDashboard movRepo;
    private final ClienteRepositoryDashboard clienteRepo;
    private final DetalleVentaRepositoryDashboard detalleRepo;

    @Transactional(readOnly = true)
    public DashBoardResumenResponse resumen(LocalDateTime desde, LocalDateTime hasta) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser mayor que hasta");
        }

        // Totales por rango (movimientos de caja)
        BigDecimal ingresosTotales = movRepo.totalPorTipo("INGRESO", desde, hasta);
        BigDecimal egresosTotales = movRepo.totalPorTipo("EGRESO", desde, hasta);
        BigDecimal gananciaTotal = ingresosTotales.subtract(egresosTotales);

        // Hoy
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(23, 59, 59);

        BigDecimal ingresosHoy = movRepo.totalPorTipo("INGRESO", inicioHoy, finHoy);
        BigDecimal egresosHoy = movRepo.totalPorTipo("EGRESO", inicioHoy, finHoy);
        BigDecimal gananciaHoy = ingresosHoy.subtract(egresosHoy);

        // Clientes
        Long numeroClientes = clienteRepo.totalClientes();

        // Vendidos (por ventas/detalles) con el mismo rango del dashboard
        BigDecimal productosVendidos = detalleRepo.cantidadVendidaPorTipo("PRODUCTO", desde, hasta);
        BigDecimal serviciosVendidos = detalleRepo.cantidadVendidaPorTipo("SERVICIO", desde, hasta);

        return new DashBoardResumenResponse(
                ingresosTotales, egresosTotales, gananciaTotal,
                ingresosHoy, egresosHoy, gananciaHoy,
                numeroClientes,
                productosVendidos, serviciosVendidos
        );
    }

    @Transactional(readOnly = true)
    public List<SerieResponse> ingresosDiarios(LocalDateTime desde, LocalDateTime hasta) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser mayor que hasta");
        }

        return movRepo.serieDiariaPorTipo("INGRESO", desde, hasta)
                .stream()
                .map(r -> new SerieResponse(r.getFecha(), r.getTotal()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SerieResponse> egresosDiarios(LocalDateTime desde, LocalDateTime hasta) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser mayor que hasta");
        }

        return movRepo.serieDiariaPorTipo("EGRESO", desde, hasta)
                .stream()
                .map(r -> new SerieResponse(r.getFecha(), r.getTotal()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopVendidoResponse> topVendidos(String tipo, LocalDateTime desde, LocalDateTime hasta, int limit) {

        if (tipo == null || (!tipo.equals("PRODUCTO") && !tipo.equals("SERVICIO"))) {
            throw new IllegalArgumentException("tipo debe ser PRODUCTO o SERVICIO");
        }
        if (limit <= 0) limit = 10;
        if (limit > 50) limit = 50;

        return detalleRepo.topVendidos(tipo, desde, hasta, limit)
                .stream()
                .map(r -> new TopVendidoResponse(r.getNombre(), r.getCantidad(), r.getTotal()))
                .toList();
    }
}
