package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Page<Pago> findByClienteId(Long clienteId, Pageable pageable);

    Page<Pago> findByVentaId(Long ventaId, Pageable pageable);

    @Query("""
        select coalesce(sum(p.monto), 0)
        from Pago p
        where p.venta.id = :ventaId and p.estado = :estado
    """)
    BigDecimal sumMontoByVentaAndEstado(@Param("ventaId") Long ventaId,
                                        @Param("estado") EstadoPago estado);

}
