package com.ansicode.SistemaAdministracionGym.asistencia;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;

public interface AsistenciaRepository  extends JpaRepository<Asistencia, Long> {
    boolean existsByClienteIdAndFechaEntradaBetween(
            Integer clienteId,
            LocalDateTime inicio,
            LocalDateTime fin
    );

    Page<Asistencia> findByClienteId(
            Integer clienteId,
            Pageable pageable
    );
}
