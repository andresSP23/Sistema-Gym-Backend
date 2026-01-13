package com.ansicode.SistemaAdministracionGym.comprobanteventa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface     ComprobanteVentaRepository extends JpaRepository<ComprobanteVenta , Long> {

    List<ComprobanteVenta> findByVentaClienteId(Long clienteId);


}