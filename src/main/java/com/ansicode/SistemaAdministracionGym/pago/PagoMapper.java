package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.venta.Venta;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
public class PagoMapper {

    public PagoResponse toResponse(Pago pago) {
        if (pago == null) return null;

        Long clienteId = null;
        String nombreCliente = null;

        // 1) Preferir snapshot (ideal para históricos)
        if (pago.getClienteIdSnapshot() != null) {
            clienteId = pago.getClienteIdSnapshot();
            nombreCliente = pago.getNombreClienteSnapshot();

            // Si hay ID pero no hay nombre (o está vacío), significa:
            // cliente existió, pero no tenemos el nombre -> "Cliente eliminado"
            if (nombreCliente == null || nombreCliente.isBlank()) {
                nombreCliente = "Cliente eliminado";
            }

        } else if (pago.getCliente() != null) {
            // 2) Fallback: pagos viejos sin snapshot
            clienteId = pago.getCliente().getId();
            String nombres = pago.getCliente().getNombres();
            String apellidos = pago.getCliente().getApellidos();
            nombreCliente = ((nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos)).trim();

            if (nombreCliente.isBlank()) {
                nombreCliente = "Cliente";
            }

        } else {
            // 3) No hay cliente asociado -> venta mostrador
            nombreCliente = "Consumidor final";
        }

        return PagoResponse.builder()
                .id(pago.getId())

                // Venta
                .ventaId(pago.getVenta() != null ? pago.getVenta().getId() : null)
                .numeroFactura(pago.getVenta() != null ? pago.getVenta().getNumeroFactura() : null)

                // Cliente
                .clienteId(clienteId)
                .nombreCliente(nombreCliente)

                // Pago
                .metodo(pago.getMetodo())
                .moneda(pago.getMoneda())
                .monto(pago.getMonto())
                .efectivoRecibido(pago.getEfectivoRecibido())
                .cambio(pago.getCambio())
                .referenciaTransaccion(pago.getReferenciaTransaccion())
                .tipoOperacion(pago.getTipoOperacion())
                .tipoComprobante(pago.getTipoComprobante())
                .estado(pago.getEstado())
                .fechaPago(pago.getFechaPago())

                // Comprobante
                .comprobanteId(pago.getComprobante() != null ? pago.getComprobante().getId() : null)

                .build();
    }
}
