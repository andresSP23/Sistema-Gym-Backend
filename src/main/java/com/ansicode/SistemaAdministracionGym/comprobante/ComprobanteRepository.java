package com.ansicode.SistemaAdministracionGym.comprobante;

import com.ansicode.SistemaAdministracionGym.enums.EstadoComprobante;
import com.ansicode.SistemaAdministracionGym.enums.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    Optional<Comprobante> findTopByVentaIdAndTipoAndEstadoOrderByCreatedAtDesc(
            Long ventaId,
            TipoComprobante tipo,
            EstadoComprobante estado
    );
}
