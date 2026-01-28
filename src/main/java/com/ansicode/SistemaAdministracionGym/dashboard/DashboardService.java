package com.ansicode.SistemaAdministracionGym.dashboard;

import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
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

        validarRangoFechas(desde, hasta);

        // Totales por rango (movimientos de caja)
        BigDecimal ingresosTotales = nz(movRepo.totalPorTipo("INGRESO", desde, hasta));
        BigDecimal egresosTotales = nz(movRepo.totalPorTipo("EGRESO", desde, hasta));
        BigDecimal gananciaTotal = ingresosTotales.subtract(egresosTotales);

        // Hoy
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(23, 59, 59);

        BigDecimal ingresosHoy = nz(movRepo.totalPorTipo("INGRESO", inicioHoy, finHoy));
        BigDecimal egresosHoy = nz(movRepo.totalPorTipo("EGRESO", inicioHoy, finHoy));
        BigDecimal gananciaHoy = ingresosHoy.subtract(egresosHoy);

        // Clientes
        Long numeroClientes = clienteRepo.totalClientes();
        if (numeroClientes == null) numeroClientes = 0L;

        // Vendidos (por ventas/detalles) con el mismo rango del dashboard
        BigDecimal productosVendidos = nz(detalleRepo.cantidadVendidaPorTipo("PRODUCTO", desde, hasta));
        BigDecimal serviciosVendidos = nz(detalleRepo.cantidadVendidaPorTipo("SERVICIO", desde, hasta));

        return new DashBoardResumenResponse(
                ingresosTotales, egresosTotales, gananciaTotal,
                ingresosHoy, egresosHoy, gananciaHoy,
                numeroClientes,
                productosVendidos, serviciosVendidos
        );
    }

    @Transactional(readOnly = true)
    public List<SerieResponse> ingresosDiarios(LocalDateTime desde, LocalDateTime hasta) {

        validarRangoFechas(desde, hasta);

        return movRepo.serieDiariaPorTipo("INGRESO", desde, hasta)
                .stream()
                .map(r -> new SerieResponse(r.getFecha(), nz(r.getTotal())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SerieResponse> egresosDiarios(LocalDateTime desde, LocalDateTime hasta) {

        validarRangoFechas(desde, hasta);

        return movRepo.serieDiariaPorTipo("EGRESO", desde, hasta)
                .stream()
                .map(r -> new SerieResponse(r.getFecha(), nz(r.getTotal())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopVendidoResponse> topVendidos(String tipo, LocalDateTime desde, LocalDateTime hasta, int limit) {

        validarTipoTopVendidos(tipo);
        validarRangoFechas(desde, hasta);

        int safeLimit = limit;
        if (safeLimit <= 0) safeLimit = 10;
        if (safeLimit > 50) safeLimit = 50;

        return detalleRepo.topVendidos(tipo, desde, hasta, safeLimit)
                .stream()
                .map(r -> new TopVendidoResponse(r.getNombre(), r.getCantidad(), nz(r.getTotal())))
                .toList();
    }

    // =========================
    // VALIDACIONES
    // =========================

    private void validarRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            // ✅ usa BusinessErrorCodes (agrega este code si no existe)
            throw new BussinessException(BusinessErrorCodes.DASHBOARD_RANGO_FECHAS_INVALIDO);
        }
    }

    private void validarTipoTopVendidos(String tipo) {
        if (tipo == null || (!tipo.equals("PRODUCTO") && !tipo.equals("SERVICIO"))) {
            // ✅ usa BusinessErrorCodes (agrega este code si no existe)
            throw new BussinessException(BusinessErrorCodes.DASHBOARD_TIPO_INVALIDO);
        }
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}