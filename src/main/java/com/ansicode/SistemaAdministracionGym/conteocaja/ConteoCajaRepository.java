package com.ansicode.SistemaAdministracionGym.conteocaja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ConteoCajaRepository extends JpaRepository<ConteoCaja, Long> {
    List<ConteoCaja> findBySesionCajaId(Long sesionCajaId);


    void deleteBySesionCajaId(Long sesionCajaId);

    @Query("""
        select coalesce(sum(c.subtotal), 0)
        from ConteoCaja c
        where c.sesionCaja.id = :sesionCajaId
          and c.moneda = :moneda
    """)
    BigDecimal totalContado(@Param("sesionCajaId") Long sesionCajaId,
                            @Param("moneda") String moneda);

}
