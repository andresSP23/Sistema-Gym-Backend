package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> , JpaSpecificationExecutor<MovimientoInventario> {
    Page<MovimientoInventario> findByProductoId(Long productoId, Pageable pageable);




}
