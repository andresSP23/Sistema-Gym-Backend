package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.membresiacliente.MembresiaCliente;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import org.springframework.stereotype.Service;

@Service
public class PagoMapper {


    // Crear entidad desde request
    public Pago toPago(PagoRequest request, Venta venta, MembresiaCliente membresiaCliente) {
        return Pago.builder()
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .fechaPago(request.getFechaPago())
                .estadoPago(request.getEstadoPago())
                .venta(venta) // puede ser null
                .membresiaCliente(membresiaCliente) // puede ser null
                .build();
    }

    // Convertir entidad a response
    public PagoResponse toPagoResponse(Pago pago) {
        PagoResponse response = new PagoResponse();
        response.setId(pago.getId());
        response.setMonto(pago.getMonto());
        response.setMetodoPago(pago.getMetodoPago());
        response.setFechaPago(pago.getFechaPago());
        response.setEstadoPago(pago.getEstadoPago());

        if (pago.getVenta() != null) {
            response.setVentaId(pago.getVenta().getId());
            response.setVentaCodigo("VENTA-" + pago.getVenta().getId()); // ejemplo de código legible
        }

        if (pago.getMembresiaCliente() != null) {
            response.setMembresiaClienteId(pago.getMembresiaCliente().getId());
            response.setClienteNombre(pago.getMembresiaCliente().getCliente().getNombres() + " " +
                    pago.getMembresiaCliente().getCliente().getApellidos());
        }

        response.setActivo(pago.getActivo());
        return response;
    }

    // Actualizar entidad desde request
    public void updatePagoFromRequest(Pago pago, PagoRequest request, Venta venta, MembresiaCliente membresiaCliente) {
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setFechaPago(request.getFechaPago());
        pago.setEstadoPago(request.getEstadoPago());
        pago.setVenta(venta); // puede ser null
        pago.setMembresiaCliente(membresiaCliente); // puede ser null
    }
}
