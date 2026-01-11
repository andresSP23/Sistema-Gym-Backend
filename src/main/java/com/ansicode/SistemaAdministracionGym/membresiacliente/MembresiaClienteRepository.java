package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembresiaClienteRepository extends JpaRepository<MembresiaCliente, Long> {

    Optional<MembresiaCliente> findByClienteIdAndEstadoIn(
            Long clienteId,
            List<EstadoMembresia> estados
    );

    Page<MembresiaCliente> findByEstado(
            EstadoMembresia estado,
            Pageable pageable
    );

    List<MembresiaCliente> findByEstadoAndFechaFinBefore(
            EstadoMembresia estado,
            LocalDate fecha
    );


    Optional<MembresiaCliente> findByClienteIdAndEstado(
            Long clienteId,
            EstadoMembresia estado
    );


}
