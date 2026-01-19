package com.ansicode.SistemaAdministracionGym.dashboard;

import com.ansicode.SistemaAdministracionGym.asistencia.Asistencia;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Producto, Long> {

//    // Clientes
//    @Query("""
//        SELECT COUNT(c)
//        FROM Cliente c
//        WHERE c.fechaRegistro BETWEEN :inicio AND :fin
//    """)
//    long clientesEnRango(LocalDate inicio, LocalDate fin);
//
//    // Asistencias
//    @Query("""
//        SELECT COUNT(a)
//        FROM Asistencia a
//        WHERE a.fechaEntrada BETWEEN :inicio AND :fin
//    """)
//    long asistenciasEnRango(LocalDateTime inicio, LocalDateTime fin);
//
//    @Query("""
//        SELECT a
//        FROM Asistencia a
//        ORDER BY a.fechaEntrada DESC
//    """)
//    Page<Asistencia> ultimaAsistencia(Pageable pageable);
//
//    // Ventas
//    @Query("""
//        SELECT COUNT(v)
//        FROM Venta v
//        WHERE v.fechaVenta BETWEEN :inicio AND :fin
//    """)
//    long totalVentas(LocalDateTime inicio, LocalDateTime fin);
//
//    @Query("""
//        SELECT COALESCE(SUM(v.total), 0)
//        FROM Venta v
//        WHERE v.fechaVenta BETWEEN :inicio AND :fin
//    """)
//    BigDecimal gananciaVentas(LocalDateTime inicio, LocalDateTime fin);
//
//    // Membresías
//    @Query("""
//        SELECT COALESCE(SUM(p.monto), 0)
//        FROM Pago p
//        WHERE p.fechaPago BETWEEN :inicio AND :fin
//    """)
//    BigDecimal gananciaMembresias(LocalDateTime inicio, LocalDateTime fin);
//
//    @Query("""
//    SELECT new com.ansicode.SistemaAdministracionGym.dashboard.MembresiaPagoDto(
//        c.nombres,
//        p.monto,
//        p.fechaPago
//    )
//    FROM Pago p
//    JOIN p.membresiaCliente mc
//    JOIN mc.cliente c
//    WHERE p.fechaPago BETWEEN :inicio AND :fin
//""")
//    List<MembresiaPagoDto> pagosMembresiaEnRango(
//            LocalDateTime inicio,
//            LocalDateTime fin
//    );
//
//    // Inventario
//    @Query("""
//        SELECT COUNT(m)
//        FROM MovimientoInventario m
//        WHERE m.fechaMovimiento BETWEEN :inicio AND :fin
//    """)
//    long movimientosInventario(LocalDateTime inicio, LocalDateTime fin);


}
