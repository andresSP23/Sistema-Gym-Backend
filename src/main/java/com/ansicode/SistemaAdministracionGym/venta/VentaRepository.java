package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;

public interface VentaRepository extends JpaRepository<Venta,Long> {


    Page<Venta> findByEstadoVenta(
            EstadoVenta estadoVenta,
            Pageable pageable
    );
}
