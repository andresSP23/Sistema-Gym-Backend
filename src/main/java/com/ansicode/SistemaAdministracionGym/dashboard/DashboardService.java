package com.ansicode.SistemaAdministracionGym.dashboard;

import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MovimientoDineroRepositoryDashboard movRepo;
    private final ClienteRepositoryDashboard clienteRepo;
    private final DetalleVentaRepositoryDashboard detalleRepo;
    private final BancoRepositoryDashboard bancoRepo;
    private final MovimientoBancoRepositoryDashboard movBancoRepo;

    // ✅ Inyecta Clock (configúralo en un @Bean con tu ZoneId)
    private final Clock clock;

    private static final long MAX_RANGO_DIAS = 366; // ajusta a tu gusto

    @Transactional(readOnly = true)
    public DashBoardResumenResponse resumen(LocalDateTime desde, LocalDateTime hasta) {

        Range r = normalizarYValidarRango(desde, hasta);

        // Totales por rango (fin exclusivo) - Caja
        BigDecimal ingresosTotales = nz(movRepo.totalPorTipo("INGRESO", r.desde(), r.hastaExclusivo()));
        BigDecimal egresosTotales = nz(movRepo.totalPorTipo("EGRESO", r.desde(), r.hastaExclusivo()));
        BigDecimal gananciaTotal = ingresosTotales.subtract(egresosTotales);

        // Hoy (fin exclusivo)
        LocalDate hoy = LocalDate.now(clock);
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime finHoyExclusivo = hoy.plusDays(1).atStartOfDay();

        BigDecimal ingresosHoy = nz(movRepo.totalPorTipo("INGRESO", inicioHoy, finHoyExclusivo));
        BigDecimal egresosHoy = nz(movRepo.totalPorTipo("EGRESO", inicioHoy, finHoyExclusivo));
        BigDecimal gananciaHoy = ingresosHoy.subtract(egresosHoy);

        // Clientes (ya tienes @Where is_visible=true, entonces el repo ya filtra)
        Long numeroClientes = nzLong(clienteRepo.totalClientes(r.desde(), r.hastaExclusivo()));

        // Vendidos (mismo rango del dashboard)
        BigDecimal productosVendidos = nz(
                detalleRepo.cantidadVendidaPorTipo("PRODUCTO", r.desde(), r.hastaExclusivo()));
        BigDecimal serviciosVendidos = nz(
                detalleRepo.cantidadVendidaPorTipo("SERVICIO", r.desde(), r.hastaExclusivo()));

        // === Métricas de Bancos ===
        BigDecimal saldoTotalBancos = nz(bancoRepo.saldoTotalBancos());
        Long numeroBancosActivos = nzLong(bancoRepo.countBancosActivos());
        BigDecimal ingresosBancariosRango = nz(movBancoRepo.ingresosBancarios(r.desde(), r.hastaExclusivo()));
        BigDecimal egresosBancariosRango = nz(movBancoRepo.egresosBancarios(r.desde(), r.hastaExclusivo()));
        BigDecimal ingresosBancariosHoy = nz(movBancoRepo.ingresosBancarios(inicioHoy, finHoyExclusivo));
        BigDecimal egresosBancariosHoy = nz(movBancoRepo.egresosBancarios(inicioHoy, finHoyExclusivo));

        return new DashBoardResumenResponse(
                ingresosTotales, egresosTotales, gananciaTotal,
                ingresosHoy, egresosHoy, gananciaHoy,
                numeroClientes,
                productosVendidos, serviciosVendidos,
                // Banco
                saldoTotalBancos, numeroBancosActivos,
                ingresosBancariosRango, egresosBancariosRango,
                ingresosBancariosHoy, egresosBancariosHoy);
    }

    @Transactional(readOnly = true)
    public List<SerieResponse> ingresosDiarios(LocalDateTime desde, LocalDateTime hasta) {

        Range r = normalizarYValidarRango(desde, hasta);

        return movRepo.serieDiariaPorTipo("INGRESO", r.desde(), r.hastaExclusivo())
                .stream()
                .map(x -> new SerieResponse(x.getFecha(), nz(x.getTotal())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SerieResponse> egresosDiarios(LocalDateTime desde, LocalDateTime hasta) {

        Range r = normalizarYValidarRango(desde, hasta);

        return movRepo.serieDiariaPorTipo("EGRESO", r.desde(), r.hastaExclusivo())
                .stream()
                .map(x -> new SerieResponse(x.getFecha(), nz(x.getTotal())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopVendidoResponse> topVendidos(String tipo, LocalDateTime desde, LocalDateTime hasta, int limit) {

        validarTipoTopVendidos(tipo);

        Range r = normalizarYValidarRango(desde, hasta);

        int safeLimit = Math.min(Math.max(limit, 10), 50);

        return detalleRepo.topVendidos(tipo, r.desde(), r.hastaExclusivo(), safeLimit)
                .stream()
                .map(x -> new TopVendidoResponse(x.getNombre(), x.getCantidad(), nz(x.getTotal())))
                .toList();
    }

    // =========================
    // RANGO: normalización + validación
    // =========================

    /**
     * Convierte el rango a fin EXCLUSIVO:
     * - si hasta viene, lo hacemos exclusivo sumando 1 nanosegundo? NO.
     * - mejor convención: repo usa "< hastaExclusivo"
     *
     * Si quieres que "hasta" sea inclusivo a nivel de usuario, puedes hacer:
     * hastaExclusivo = hasta.plusSeconds(1) si viene sin nanos
     * pero lo correcto es: UI mande hasta como fin del día o usar fechas.
     */
    private Range normalizarYValidarRango(LocalDateTime desde, LocalDateTime hasta) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BussinessException(BusinessErrorCodes.DASHBOARD_RANGO_FECHAS_INVALIDO);
        }

        // ✅ Convención: hastaExclusivo = hasta + 1 nanosegundo NO es buena idea.
        // Mejor: usa < hastaExclusivo en SQL, y aquí definimos:
        // - si hasta es null -> null (sin límite)
        // - si hasta NO es null -> lo convertimos a "hastaExclusivo" sumando 1 segundo
        // si tú trabajas a segundos,
        // o sumando 1 día si tu "hasta" suele ser fecha al inicio del día.
        //
        // Para ser neutros: si viene hasta, lo dejamos tal cual, y en repos tú decides
        // <= o <.
        // Pero yo recomiendo < y que "hasta" venga como fin deseado.
        LocalDateTime hastaExclusivo = hasta;

        if (desde != null && hasta != null) {
            long dias = Duration.between(desde, hasta).toDays();
            if (dias > MAX_RANGO_DIAS) {
                throw new BussinessException(BusinessErrorCodes.DASHBOARD_RANGO_MUY_GRANDE);
            }
        }

        return new Range(desde, hastaExclusivo);
    }

    private void validarTipoTopVendidos(String tipo) {
        if (tipo == null || (!tipo.equals("PRODUCTO") && !tipo.equals("SERVICIO"))) {
            throw new BussinessException(BusinessErrorCodes.DASHBOARD_TIPO_INVALIDO);
        }
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private Long nzLong(Long v) {
        return v == null ? 0L : v;
    }

    private record Range(LocalDateTime desde, LocalDateTime hastaExclusivo) {
    }
}