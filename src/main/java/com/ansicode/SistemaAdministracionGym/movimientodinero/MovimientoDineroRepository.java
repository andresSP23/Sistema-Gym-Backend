package com.ansicode.SistemaAdministracionGym.movimientodinero;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface MovimientoDineroRepository extends JpaRepository<MovimientoDinero, Long>  , JpaSpecificationExecutor<MovimientoDinero> {
    Page<MovimientoDinero> findAll(Pageable pageable);


    @Query("""
        select coalesce(sum(
            case
                when m.tipo = 'INGRESO' then m.monto
                when m.tipo = 'EGRESO' then -m.monto
                else 0
            end
        ), 0)
        from MovimientoDinero m
        where m.sesionCaja.id = :sesionCajaId
          and m.metodo = 'EFECTIVO'
          and m.moneda = :moneda
    """)
    BigDecimal netoEfectivoPorSesion(@Param("sesionCajaId") Long sesionCajaId,
                                     @Param("moneda") String moneda);

}
