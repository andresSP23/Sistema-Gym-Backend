package com.ansicode.SistemaAdministracionGym.cliente;

import org.springframework.stereotype.Service;

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
                .edad(request.getEdad())
                .codigoInterno(request.getCodigoInterno())
                .fechaRegistro(request.getFechaRegistro())
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
                .edad(cliente.getEdad())
                .codigoInterno(cliente.getCodigoInterno())
                .fechaRegistro(cliente.getFechaRegistro())
                .activo(cliente.getActivo()) // activo del borrado lógico
                .build();
    }

//    // Mapear actualización (opcional)
//    public void updateClienteFromRequest(Cliente cliente, ClienteRequest request) {
//        cliente.setCedula(request.getCedula());
//        cliente.setNombres(request.getNombres());
//        cliente.setApellidos(request.getApellidos());
//        cliente.setEmail(request.getEmail());
//        cliente.setTelefono(request.getTelefono());
//        cliente.setDireccion(request.getDireccion());
//        cliente.setEdad(request.getEdad());
//        cliente.setCodigoInterno(request.getCodigoInterno());
//        cliente.setFechaRegistro(request.getFechaRegistro());
//    }
}
