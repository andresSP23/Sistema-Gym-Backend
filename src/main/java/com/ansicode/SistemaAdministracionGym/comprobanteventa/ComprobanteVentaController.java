package com.ansicode.SistemaAdministracionGym.comprobanteventa;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comprobante-venta")
@Tag(name = "Comprobante Venta")
@RequiredArgsConstructor
public class ComprobanteVentaController {

    private final ComprobanteVentaService comprobanteVentaService;


    @GetMapping("/listar-comprobantes/{clienteId}")
    public List<ComprobanteVentaResponse> listarComprobantes(@PathVariable Long clienteId) {
        return comprobanteVentaService.listarComprobantesPorCliente(clienteId);
    }

    @GetMapping("/pdf/{comprobanteId}")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long comprobanteId) {
        byte[] pdf = comprobanteVentaService.descargarPdf(comprobanteId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=comprobante_" + comprobanteId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
