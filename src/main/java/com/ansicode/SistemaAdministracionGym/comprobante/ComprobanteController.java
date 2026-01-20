package com.ansicode.SistemaAdministracionGym.comprobante;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("comprobante")
@Tag(name = "Comprobante")
@RequiredArgsConstructor
public class ComprobanteController {
    private final ComprobanteService  comprobanteService;

    @GetMapping("/comprobantes/pdf/{comprobanteId}")
    public ResponseEntity<Resource> descargarPdf(@PathVariable Long comprobanteId) {
        return comprobanteService.descargarPdf(comprobanteId);
    }
}
