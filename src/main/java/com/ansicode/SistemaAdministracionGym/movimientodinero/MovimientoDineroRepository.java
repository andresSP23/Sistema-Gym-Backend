package com.ansicode.SistemaAdministracionGym.movimientodinero;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MovimientoDineroRepository extends JpaRepository<MovimientoDinero, Long>  , JpaSpecificationExecutor<MovimientoDinero> {
    Page<MovimientoDinero> findAll(Pageable pageable);

}
