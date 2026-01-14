package com.ansicode.SistemaAdministracionGym.comprobantepago;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Long> {
    List<ComprobantePago> findByPagoId(Long pagoId);
}
