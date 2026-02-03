package com.ansicode.SistemaAdministracionGym.gasto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface GastoRepository extends JpaRepository<Gasto, Long>, JpaSpecificationExecutor<Gasto> {

    @Query("SELECT SUM(g.monto) FROM Gasto g WHERE g.sucursalId = :sucursalId AND g.fechaGasto BETWEEN :start AND :end AND g.estado = 'PAGADO'")
    BigDecimal sumGastosPorMes(Long sucursalId, LocalDate start, LocalDate end);
}
