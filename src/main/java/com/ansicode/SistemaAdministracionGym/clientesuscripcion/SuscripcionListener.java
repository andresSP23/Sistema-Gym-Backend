package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.pago.event.PagoCompletadoEvent;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuscripcionListener {

    private final ClienteSuscripcionService clienteSuscripcionService;

    @EventListener
    public void onPagoCompletado(PagoCompletadoEvent event) {
        Venta venta = event.getPago().getVenta();

        if (venta == null || venta.getDetalles() == null) {
            return;
        }

        boolean tieneServicio = venta.getDetalles().stream()
                .anyMatch(d -> d.getTipoItem() == TipoItemVenta.SERVICIO);

        if (tieneServicio) {
            clienteSuscripcionService.registrarSuscripcionDesdeVenta(
                    venta,
                    event.getPago().getFechaPago());
        }
    }
}
