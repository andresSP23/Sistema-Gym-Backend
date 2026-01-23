package com.ansicode.SistemaAdministracionGym.sesioncaja;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SesionCajaRepository extends JpaRepository<SesionCaja, Long> {
    Optional<SesionCaja> findFirstBySucursalIdAndEstadoOrderByFechaAperturaDesc(Long sucursalId, EstadoSesionCaja estado);

    Optional<SesionCaja> findFirstByUsuarioAperturaIdAndEstadoOrderByFechaAperturaDesc(Long usuarioAperturaId, EstadoSesionCaja estado);

    @Query("""
SELECT new com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaResponse(
    s.id,
    s.sucursalId,
    s.usuarioAperturaId,
    u1.nombre,
    s.fechaApertura,
    s.baseInicialEfectivo,
    s.estado,
    s.fechaCierre,
    s.usuarioCierreId,
    u2.nombre,
    s.observacion,
    c.efectivoContado,
    c.diferencia
)
FROM SesionCaja s
LEFT JOIN CuadreCaja c ON c.sesionCaja.id = s.id
LEFT JOIN User u1 ON u1.id = s.usuarioAperturaId
LEFT JOIN User u2 ON u2.id = s.usuarioCierreId
""")
    Page<SesionCajaResponse> findAllWithSaldoFinal(Pageable pageable);
}
