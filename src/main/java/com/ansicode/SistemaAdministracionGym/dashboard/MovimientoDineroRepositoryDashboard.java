package com.ansicode.SistemaAdministracionGym.dashboard;

import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDinero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoDineroRepositoryDashboard extends JpaRepository<MovimientoDinero, Long> {
  @Query(value = """
          SELECT COALESCE(SUM(m.monto), 0)
          FROM movimientos_dinero m
          WHERE m.tipo = :tipo
            AND m.metodo != 'OTRO'
            AND m.fecha >= COALESCE(:desde, m.fecha)
            AND m.fecha <= COALESCE(:hasta, m.fecha)
      """, nativeQuery = true)
  BigDecimal totalPorTipo(
      @Param("tipo") String tipo, // 'INGRESO' o 'EGRESO'
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta);

  @Query(value = """
          SELECT DATE(m.fecha) AS fecha,
                 COALESCE(SUM(m.monto), 0) AS total
          FROM movimientos_dinero m
          WHERE m.tipo = :tipo
            AND m.metodo != 'OTRO'
            AND m.fecha >= COALESCE(:desde, m.fecha)
            AND m.fecha <= COALESCE(:hasta, m.fecha)
          GROUP BY DATE(m.fecha)
          ORDER BY DATE(m.fecha)
      """, nativeQuery = true)
  List<SerieRow> serieDiariaPorTipo(
      @Param("tipo") String tipo,
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta);
}
