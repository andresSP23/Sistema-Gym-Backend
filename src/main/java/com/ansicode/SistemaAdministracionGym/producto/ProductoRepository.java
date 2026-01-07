package com.ansicode.SistemaAdministracionGym.producto;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByStockGreaterThan(
            Integer stock,
            Pageable pageable
    );
}
