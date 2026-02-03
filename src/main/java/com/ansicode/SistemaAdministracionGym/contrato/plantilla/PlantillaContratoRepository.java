package com.ansicode.SistemaAdministracionGym.contrato.plantilla;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantillaContratoRepository extends JpaRepository<PlantillaContrato, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT p FROM PlantillaContrato p WHERE p.activo = true ORDER BY p.createdAt DESC")
    java.util.List<PlantillaContrato> findActivas(org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE PlantillaContrato p SET p.activo = false WHERE p.id <> ?1")
    void deactivateAllExcept(Long id);

    default Optional<PlantillaContrato> findDefaultActive() {
        return findActivas(org.springframework.data.domain.PageRequest.of(0, 1)).stream().findFirst();
    }
}
