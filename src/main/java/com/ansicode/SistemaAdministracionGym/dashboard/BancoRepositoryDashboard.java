package com.ansicode.SistemaAdministracionGym.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ansicode.SistemaAdministracionGym.banco.Banco;

import java.math.BigDecimal;

/**
 * Repository para consultas de dashboard de Bancos.
 */
@Repository
public interface BancoRepositoryDashboard extends JpaRepository<Banco, Long> {

    /**
     * Suma el saldo de todos los bancos activos.
     */
    @Query("SELECT COALESCE(SUM(b.saldo), 0) FROM Banco b WHERE b.isVisible = true AND b.activo = true")
    BigDecimal saldoTotalBancos();

    /**
     * Cuenta el número de bancos activos.
     */
    @Query("SELECT COUNT(b) FROM Banco b WHERE b.isVisible = true AND b.activo = true")
    Long countBancosActivos();
}
