package com.ansicode.SistemaAdministracionGym.comprobante;

import com.ansicode.SistemaAdministracionGym.enums.EstadoComprobante;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import com.ansicode.SistemaAdministracionGym.pago.PagoRepository;
import com.ansicode.SistemaAdministracionGym.pago.event.PagoCompletadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ComprobanteListener {

    private final ComprobanteRepository comprobanteRepository;
    private final ComprobanteService comprobanteService;
    private final PagoRepository pagoRepository;

    @EventListener
    public void onPagoCompletado(PagoCompletadoEvent event) {
        Pago pago = event.getPago();

        // Logic extracted from PagoService
        // Use a new transaction or no transaction context to avoid blocking the main
        // one if it were open (it's not, event is published after?)
        // Actually, we want this to be fail-safe. If it fails, we log but don't
        // rollback everything (since payment is done).

        try {
            generateAndLinkComprobante(pago);
        } catch (Exception e) {
            System.err.println("Error generando PDF para Pago ID " + pago.getId() + ": " + e.getMessage());
            // We don't rethrow to avoid breaking the caller if this is synchronous
        }
    }

    private void generateAndLinkComprobante(Pago pago) {
        Optional<Comprobante> comprobanteOpt = comprobanteRepository
                .findTopByVentaIdAndTipoAndEstadoOrderByCreatedAtDesc(
                        pago.getVenta().getId(),
                        pago.getTipoComprobante(),
                        EstadoComprobante.GENERADO);

        Comprobante comprobante;
        if (comprobanteOpt.isPresent() &&
                comprobanteOpt.get().getPdfRef() != null &&
                !comprobanteOpt.get().getPdfRef().isBlank()) {

            comprobante = comprobanteOpt.get();
        } else {
            // This service method likely handles its own transaction or file I/O
            comprobante = comprobanteService.generarFacturaPdf(pago.getVenta());
        }

        // Update Pago in DB
        // We use a separate method call or repo to avoid full entity save if possible,
        // or just save.
        // PagoService used a custom query updateComprobante.
        updatePagoComprobante(pago.getId(), comprobante);

        // Update the object in memory so the response contains it
        pago.setComprobante(comprobante);
        pago.setTipoComprobante(comprobante.getTipo());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updatePagoComprobante(Long pagoId, Comprobante comprobante) {
        pagoRepository.updateComprobante(pagoId, comprobante);
    }
}
