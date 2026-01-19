package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta,Long> {


    @Query("""
           select v.numeroFactura
           from Venta v
           where v.sucursal.id = :sucursalId
           order by v.id desc
           """)
    List<String> findUltimosNumerosFacturaPorSucursal(@Param("sucursalId") Long sucursalId, Pageable pageable);
}
