package com.ansicode.SistemaAdministracionGym.cliente;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClienteMapper {

    // Crear o mapear request a entidad
    public Cliente toCliente(ClienteRequest request) {
        return Cliente.builder()
                .cedula(request.getCedula())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .fechaNacimiento(request.getFechaNacimiento())
                .build();
    }

    // Mapear entidad a response
    public ClienteResponse toClienteResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .cedula(cliente.getCedula())
                .nombres(cliente.getNombres())
                .apellidos(cliente.getApellidos())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .codigoInterno(cliente.getCodigoInterno())
                .build();
    }

    // Mapear actualización (opcional)
    public void updateClienteFromRequest(Cliente cliente, ClienteRequest request) {
        cliente.setCedula(request.getCedula());
        cliente.setNombres(request.getNombres());
        cliente.setApellidos(request.getApellidos());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setFechaNacimiento(request.getFechaNacimiento());
    }
}
