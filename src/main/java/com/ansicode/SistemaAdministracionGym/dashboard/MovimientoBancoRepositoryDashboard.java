package com.ansicode.SistemaAdministracionGym.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ansicode.SistemaAdministracionGym.banco.movimiento.MovimientoBanco;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Repository para consultas de dashboard de MovimientoBanco.
 */
@Repository
public interface MovimientoBancoRepositoryDashboard extends JpaRepository<MovimientoBanco, Long> {

        /**
         * Ingresos bancarios en rango - usando native query para evitar problemas de
         * PostgreSQL con nulls.
         */
        @Query(value = "SELECT COALESCE(SUM(m.monto), 0) FROM movimientos_banco m " +
                        "WHERE m.is_visible = true " +
                        "AND m.tipo = 'INGRESO' " +
                        "AND (CAST(:desde AS TIMESTAMP) IS NULL OR m.fecha >= :desde) " +
                        "AND (CAST(:hasta AS TIMESTAMP) IS NULL OR m.fecha < :hasta)", nativeQuery = true)
        BigDecimal ingresosBancariosNative(
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta);

        /**
         * Egresos bancarios en rango - usando native query para evitar problemas de
         * PostgreSQL con nulls.
         */
        @Query(value = "SELECT COALESCE(SUM(m.monto), 0) FROM movimientos_banco m " +
                        "WHERE m.is_visible = true " +
                        "AND m.tipo = 'EGRESO' " +
                        "AND (CAST(:desde AS TIMESTAMP) IS NULL OR m.fecha >= :desde) " +
                        "AND (CAST(:hasta AS TIMESTAMP) IS NULL OR m.fecha < :hasta)", nativeQuery = true)
        BigDecimal egresosBancariosNative(
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta);

        /**
         * Ingresos bancarios en rango.
         */
        default BigDecimal ingresosBancarios(LocalDateTime desde, LocalDateTime hasta) {
                return ingresosBancariosNative(desde, hasta);
        }

        /**
         * Egresos bancarios en rango.
         */
        default BigDecimal egresosBancarios(LocalDateTime desde, LocalDateTime hasta) {
                return egresosBancariosNative(desde, hasta);
        }
}
