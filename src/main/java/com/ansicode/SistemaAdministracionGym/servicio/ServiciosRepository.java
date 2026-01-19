package com.ansicode.SistemaAdministracionGym.servicio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiciosRepository extends JpaRepository<Servicios,Long> {


    Page<Servicios> findByEsSuscripcionAndEstado(Boolean esSuscripcion, Boolean estado, Pageable pageable);

    List<Servicios> findByEsSuscripcionAndEstadoOrderByNombreAsc(Boolean esSuscripcion, Boolean estado);
}
