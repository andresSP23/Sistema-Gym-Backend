package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoBancoRepository extends JpaRepository<MovimientoBanco, Long> {
    java.util.List<MovimientoBanco> findByBancoId(Long bancoId);
}
