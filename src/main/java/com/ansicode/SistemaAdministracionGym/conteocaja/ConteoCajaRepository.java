package com.ansicode.SistemaAdministracionGym.conteocaja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ConteoCajaRepository extends JpaRepository<ConteoCaja, Long> {
    List<ConteoCaja> findBySesionCajaId(Long sesionCajaId);






    @Modifying
    @Query("DELETE FROM ConteoCaja c WHERE c.sesionCaja.id = :sesionCajaId")
    void deleteBySesionCajaId(Long sesionCajaId);

    List<ConteoCaja> findBySesionCaja_IdOrderByDenominacionDesc(Long sesionCajaId);

    @Query("""
        SELECT COALESCE(SUM(c.subtotal), 0)
        FROM ConteoCaja c
        WHERE c.sesionCaja.id = :sesionCajaId
          AND (:moneda IS NULL OR c.moneda = :moneda)
    """)
    BigDecimal totalContado(Long sesionCajaId, String moneda);

}
