package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PagoRepository extends JpaRepository<Pago, Long>, JpaSpecificationExecutor<Pago> {

  Page<Pago> findByClienteId(Long clienteId, Pageable pageable);

  Page<Pago> findByVentaId(Long ventaId, Pageable pageable);

  @Query("""
          select coalesce(sum(p.monto), 0)
          from Pago p
          where p.venta.id = :ventaId and p.estado = :estado
      """)
  BigDecimal sumMontoByVentaAndEstado(@Param("ventaId") Long ventaId,
      @Param("estado") EstadoPago estado);

  @org.springframework.transaction.annotation.Transactional
  @org.springframework.data.jpa.repository.Modifying
  @Query("UPDATE Pago p SET p.comprobante = :comprobante, p.tipoComprobante = :#{#comprobante.tipo} WHERE p.id = :id")
  void updateComprobante(@Param("id") Long id,
      @Param("comprobante") com.ansicode.SistemaAdministracionGym.comprobante.Comprobante comprobante);
}
