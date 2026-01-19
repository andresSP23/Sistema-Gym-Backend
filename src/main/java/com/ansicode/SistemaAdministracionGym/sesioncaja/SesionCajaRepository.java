package com.ansicode.SistemaAdministracionGym.sesioncaja;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SesionCajaRepository extends JpaRepository<SesionCaja, Long> {
    Optional<SesionCaja> findFirstBySucursalIdAndEstadoOrderByFechaAperturaDesc(Long sucursalId, EstadoSesionCaja estado);

    Optional<SesionCaja> findFirstByUsuarioAperturaIdAndEstadoOrderByFechaAperturaDesc(Long usuarioAperturaId, EstadoSesionCaja estado);
}
