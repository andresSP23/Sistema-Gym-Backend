package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.venta.Venta;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Component
public class PagoMapper {

    public PagoResponse toResponse(Pago pago) {

        if (pago == null) return null;

        return PagoResponse.builder()
                .id(pago.getId())

                // Venta
                .ventaId(pago.getVenta() != null ? pago.getVenta().getId() : null)
                .numeroFactura(
                        pago.getVenta() != null ? pago.getVenta().getNumeroFactura() : null
                )

                // Cliente
                .clienteId(
                        pago.getCliente() != null ? pago.getCliente().getId() : null
                )
                .nombreCliente(
                        pago.getCliente() != null
                                ? pago.getCliente().getNombres() + " " + pago.getCliente().getApellidos()
                                : null
                )

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
                .comprobanteId(
                        pago.getComprobante() != null ? pago.getComprobante().getId() : null
                )
                .pdfRef(
                        pago.getComprobante() != null ? pago.getComprobante().getPdfRef() : null
                )

                .build();
    }
}