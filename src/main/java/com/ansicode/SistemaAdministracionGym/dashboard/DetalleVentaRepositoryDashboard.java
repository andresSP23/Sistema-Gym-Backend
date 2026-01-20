package com.ansicode.SistemaAdministracionGym.dashboard;

import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DetalleVentaRepositoryDashboard extends JpaRepository<DetalleVenta, Long> {


    @Query(value = """
        SELECT COALESCE(SUM(d.cantidad), 0)
        FROM venta_detalles d
        INNER JOIN ventas v ON v.id = d.venta_id
        WHERE d.tipo_item = :tipo
          AND v.fecha_venta >= COALESCE(:desde, v.fecha_venta)
          AND v.fecha_venta <= COALESCE(:hasta, v.fecha_venta)
    """, nativeQuery = true)
    BigDecimal cantidadVendidaPorTipo(
            @Param("tipo") String tipo, // 'PRODUCTO' o 'SERVICIO'
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    @Query(value = """
        SELECT d.descripcion_snapshot AS nombre,
               COALESCE(SUM(d.cantidad), 0) AS cantidad,
               COALESCE(SUM(d.total_linea), 0) AS total
        FROM venta_detalles d
        INNER JOIN ventas v ON v.id = d.venta_id
        WHERE d.tipo_item = :tipo
          AND v.fecha_venta >= COALESCE(:desde, v.fecha_venta)
          AND v.fecha_venta <= COALESCE(:hasta, v.fecha_venta)
        GROUP BY d.descripcion_snapshot
        ORDER BY COALESCE(SUM(d.cantidad), 0) DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<TopRow> topVendidos(
            @Param("tipo") String tipo,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("limit") int limit
    );
}
