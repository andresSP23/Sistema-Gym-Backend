package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import org.springframework.stereotype.Service;

@Service
public class ContratoMapper {
    // Mapear request a entidad (crear)
    public Contrato toContrato(ContratoRequest request, Cliente cliente) {
        return Contrato.builder()
                .cliente(cliente)
                .archivoUrl(request.getArchivoUrl())
                .estadoContrato(request.getEstadoContrato())
                .build();
    }

    // Mapear entidad a response
    public ContratoResponse toContratoResponse(Contrato contrato) {
        return ContratoResponse.builder()
                .id(contrato.getId())
                .clienteId(contrato.getCliente().getId())
                .clienteNombre(contrato.getCliente().getNombres() + " " + contrato.getCliente().getApellidos())
                .archivoUrl(contrato.getArchivoUrl())
                .contenidoContrato(contrato.getContenidoContrato())
                .estadoContrato(contrato.getEstadoContrato())
                .activo(contrato.getIsVisible())
                .fechaFirma(contrato.getFechaFirma())
                .fechaGeneracion(contrato.getCreatedAt())
                .build();
    }

    // Mapear actualización
    public void updateContratoFromRequest(Contrato contrato, ContratoRequest request, Cliente cliente) {
        contrato.setCliente(cliente);
        contrato.setArchivoUrl(request.getArchivoUrl());
        contrato.setEstadoContrato(request.getEstadoContrato());
    }
}
