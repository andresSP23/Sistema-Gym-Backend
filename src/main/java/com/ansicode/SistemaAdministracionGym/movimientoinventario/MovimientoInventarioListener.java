package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.pago.event.PagoCompletadoEvent;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovimientoInventarioListener {

    private final MovimientoInventarioService movimientoInventarioService;
    private final ProductoRepository productoRepository;

    @EventListener
    public void onPagoCompletado(PagoCompletadoEvent event) {
        Venta venta = event.getPago().getVenta();

        if (venta == null || venta.getDetalles() == null) {
            return;
        }

        for (DetalleVenta d : venta.getDetalles()) {
            if (d.getTipoItem() != TipoItemVenta.PRODUCTO) {
                continue;
            }

            if (d.getCantidad() == null) {
                // Should be validated before, but just in case
                continue;
            }

            int unidades;
            try {
                unidades = d.getCantidad().intValueExact();
            } catch (ArithmeticException ex) {
                // Ignore weird quantities in listener or log error?
                // Since validation happens in PagoService, we assume data is correct here
                // or we could throw exception to rollback if transaction phase allows.
                // For now, let's assume valid data.
                continue;
            }

            if (unidades <= 0)
                continue;

            Producto producto = productoRepository.findById(d.getReferenciaId())
                    .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PAGO_PRODUCTO_NOT_FOUND));

            movimientoInventarioService.registrarSalida(
                    producto,
                    unidades,
                    "Salida por venta " + venta.getNumeroFactura());

            // Persist updated stock
            productoRepository.save(producto);
        }
    }
}
