package com.ansicode.SistemaAdministracionGym.contrato;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContratoRepository  extends JpaRepository<Contrato, Long> {

    Optional<Contrato> findTopByClienteIdOrderByCreatedDateDesc(
            Integer clienteId
    );
}
