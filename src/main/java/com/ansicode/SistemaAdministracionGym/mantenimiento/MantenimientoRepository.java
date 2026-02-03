package com.ansicode.SistemaAdministracionGym.mantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT m FROM Mantenimiento m WHERE m.equipamiento.id = ?1 ORDER BY m.fechaRealizacion DESC")
    List<Mantenimiento> findByEquipamientoId(Long equipamientoId);
}
