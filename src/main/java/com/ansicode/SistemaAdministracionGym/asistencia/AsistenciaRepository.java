package com.ansicode.SistemaAdministracionGym.asistencia;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
        boolean existsByClienteIdAndFechaEntradaBetween(
                        Long clienteId,
                        LocalDateTime inicio,
                        LocalDateTime fin);

        Page<Asistencia> findByClienteId(
                        Long clienteId,
                        Pageable pageable);

        java.util.List<Asistencia> findTop10ByOrderByFechaEntradaDesc();
}
