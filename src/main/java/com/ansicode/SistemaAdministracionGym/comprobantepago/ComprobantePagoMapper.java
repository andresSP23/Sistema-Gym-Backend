package com.ansicode.SistemaAdministracionGym.comprobantepago;

import com.ansicode.SistemaAdministracionGym.membresiacliente.MembresiaCliente;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import org.springframework.stereotype.Component;

@Component
public class ComprobantePagoMapper {

    public ComprobantePagoResponse toResponse(ComprobantePago entity) {

        Pago pago = entity.getPago();
        MembresiaCliente mc = pago.getMembresiaCliente();

        return ComprobantePagoResponse.builder()
                .id(entity.getId())
                .pagoId(pago.getId())
                .contenido(entity.getContenido())
                .fechaGeneracion(entity.getFechaGeneracion())
                .activo(entity.getActivo())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estadoPago(pago.getEstadoPago())
                .membresiaClienteId(mc.getId())
                .clienteNombre(mc.getCliente().getNombreCompleto() )
                .build();
    }
}
