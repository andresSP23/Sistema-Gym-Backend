package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.enums.EstadoEquipamiento;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;

public interface EquipamientoRepository extends JpaRepository<Equipamiento, Long> {


    Page<Equipamiento> findByEstadoEquipamiento(
            EstadoEquipamiento estado,
            Pageable pageable
    );
}
