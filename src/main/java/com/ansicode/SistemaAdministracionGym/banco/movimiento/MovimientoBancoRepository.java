package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoBancoRepository
        extends JpaRepository<MovimientoBanco, Long>, JpaSpecificationExecutor<MovimientoBanco> {
    List<MovimientoBanco> findByBancoId(Long bancoId);

    Page<MovimientoBanco> findByBancoIdOrderByFechaDescIdDesc(Long bancoId, Pageable pageable);
}
