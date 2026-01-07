package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.membresia.Membresia;
import org.springframework.stereotype.Service;

@Service
public class MembresiaClienteMapper {


    // Crear entidad desde request
    public MembresiaCliente toMembresiaCliente(MembresiaClienteRequest request, Cliente cliente, Membresia membresia) {
        return MembresiaCliente.builder()
                .cliente(cliente)
                .membresia(membresia)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .estado(request.getEstado())
                .build();
    }

    // Convertir entidad a response
    public MembresiaClienteResponse toMembresiaClienteResponse(MembresiaCliente membresiaCliente) {
        return MembresiaClienteResponse.builder()
                .id(membresiaCliente.getId())
                .clienteId(membresiaCliente.getCliente().getId())
                .clienteNombre(membresiaCliente.getCliente().getNombres() + " " + membresiaCliente.getCliente().getApellidos())
                .membresiaId(membresiaCliente.getMembresia().getId())
                .membresiaNombre(membresiaCliente.getMembresia().getNombre())
                .fechaInicio(membresiaCliente.getFechaInicio())
                .fechaFin(membresiaCliente.getFechaFin())
                .estado(membresiaCliente.getEstado())
                .activo(membresiaCliente.getActivo())
                .build();
    }

    // Actualizar entidad desde request
    public void updateMembresiaClienteFromRequest(MembresiaCliente membresiaCliente, MembresiaClienteRequest request, Cliente cliente, Membresia membresia) {
        membresiaCliente.setCliente(cliente);
        membresiaCliente.setMembresia(membresia);
        membresiaCliente.setFechaInicio(request.getFechaInicio());
        membresiaCliente.setFechaFin(request.getFechaFin());
        membresiaCliente.setEstado(request.getEstado());
    }

}
