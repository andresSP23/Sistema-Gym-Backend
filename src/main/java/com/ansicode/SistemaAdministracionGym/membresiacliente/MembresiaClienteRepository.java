package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Optional;

public interface MembresiaClienteRepository extends JpaRepository<MembresiaCliente, Long> {

    Optional<MembresiaCliente> findByClienteIdAndEstado(
            Integer clienteId,
            EstadoMembresia estado
    );

    Page<MembresiaCliente> findByEstado(
            EstadoMembresia estado,
            Pageable pageable
    );
}
