package com.ansicode.SistemaAdministracionGym.comprobantepago;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("comprobante-pago")
@Tag(name = "Comprobante Pago")
@RequiredArgsConstructor
public class ComprobantePagoController {

    private final ComprobantePagoService comprobantePagoService;


    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ComprobantePagoResponse>> listarPorCliente(
            @PathVariable Long clienteId
    ) {
        return ResponseEntity.ok(
                comprobantePagoService.listarComprobantesPorCliente(clienteId)
        );
    }

    /**
     * 🔹 Descargar PDF del comprobante
     */
    @GetMapping("/pdf/{comprobanteId}/")
    public ResponseEntity<byte[]> descargarPdf(
            @PathVariable Long comprobanteId
    ) {

        byte[] pdfBytes = comprobantePagoService.descargarPdf(comprobanteId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=comprobante-pago-" + comprobanteId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
