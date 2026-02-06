package com.ansicode.SistemaAdministracionGym.contrato;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ContratoRepository extends JpaRepository<Contrato, Long>, JpaSpecificationExecutor<Contrato> {

    Optional<Contrato> findTopByClienteIdOrderByCreatedAtDesc(Integer clienteId);

    Optional<Contrato> findBySuscripcionId(Long suscripcionId);
}
