package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClienteSuscripcionRepository extends JpaRepository<ClienteSuscripcion, Long> {

    Optional<ClienteSuscripcion> findTopByClienteIdAndEstadoAndFechaFinAfterOrderByFechaFinDesc(
            Long clienteId,
            EstadoSuscripcion estado,
            LocalDateTime fecha
    );
    boolean existsByVentaId(Long ventaId);

    List<ClienteSuscripcion> findByEstadoAndFechaFinBefore(
            EstadoSuscripcion estado,
            LocalDateTime fecha
    );

    @Query("""
           select cs
           from ClienteSuscripcion cs
           where cs.cliente.id = :clienteId
             and cs.estado = 'ACTIVA'
             and cs.fechaFin >= :ahora
           order by cs.fechaFin desc
           """)
    List<ClienteSuscripcion> findActivaVigente(@Param("clienteId") Long clienteId,
                                               @Param("ahora") LocalDateTime ahora);
}

;