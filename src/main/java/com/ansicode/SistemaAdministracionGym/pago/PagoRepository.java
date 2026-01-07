package com.ansicode.SistemaAdministracionGym.pago;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Page<Pago> findByMembresiaClienteId(
            Integer membresiaClienteId,
            Pageable pageable
    );

    Page<Pago> findByVentaId(
            Integer ventaId,
            Pageable pageable
    );
}
