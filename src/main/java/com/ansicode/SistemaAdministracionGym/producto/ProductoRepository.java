package com.ansicode.SistemaAdministracionGym.producto;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByStockGreaterThan(
            Integer stock,
            Pageable pageable
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);

    @Query("""
    SELECT COUNT(p)
    FROM Producto p
    WHERE (
        COALESCE((
            SELECT SUM(mi.cantidad) 
            FROM MovimientoInventario mi 
            WHERE mi.producto = p AND mi.tipoMovimiento = 'ENTRADA'
        ), 0) -
        COALESCE((
            SELECT SUM(mi.cantidad) 
            FROM MovimientoInventario mi 
            WHERE mi.producto = p AND mi.tipoMovimiento = 'SALIDA'
        ), 0)
    ) < :minimo
""")
    long countProductosConStockBajo(@Param("minimo") Integer minimo);
}
