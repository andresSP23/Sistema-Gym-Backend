package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.membresia.Membresia;
import org.springframework.stereotype.Service;

@Service
public class MembresiaClienteMapper {

    /* =====================
   CREATE
   ===================== */
    public MembresiaCliente toNewEntity(
            Cliente cliente,
            Membresia membresia
    ) {
        return MembresiaCliente.builder()
                .cliente(cliente)
                .membresia(membresia)
                .estado(EstadoMembresia.PENDIENTE_PAGO)
                .activo(true)
                .build();
    }

    /* =====================
       UPDATE (solo asignación)
       ===================== */
    public void updateAsignacion(
            MembresiaCliente mc,
            Cliente cliente,
            Membresia membresia
    ) {
        mc.setCliente(cliente);
        mc.setMembresia(membresia);
    }

    /* =====================
       RESPONSE
       ===================== */
    public MembresiaClienteResponse toResponse(MembresiaCliente mc) {
        return MembresiaClienteResponse.builder()
                .id(mc.getId())
                .clienteId(mc.getCliente().getId())
                .clienteNombre(
                        mc.getCliente().getNombres() + " " +
                                mc.getCliente().getApellidos()
                )
                .membresiaId(mc.getMembresia().getId())
                .membresiaNombre(mc.getMembresia().getNombre())
                .fechaInicio(mc.getFechaInicio())
                .fechaFin(mc.getFechaFin())
                .estado(mc.getEstado())
                .activo(mc.getActivo())
                .build();
    }
}
