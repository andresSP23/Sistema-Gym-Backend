package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

@RestController
@RequestMapping("contratos")
@Tag(name = "Contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService contratoService;

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    public ResponseEntity<ContratoResponse> create(
            @RequestBody @Valid ContratoRequest request) {
        return ResponseEntity.ok(contratoService.create(request));
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    public ResponseEntity<ContratoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ContratoRequest request) {
        return ResponseEntity.ok(contratoService.update(id, request));
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    public ResponseEntity<ContratoResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(contratoService.findById(id));
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    public ResponseEntity<PageResponse<ContratoResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(contratoService.findAll(pageable));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        contratoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Long id) {
        byte[] pdfBytes = contratoService.generarContratoPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "contrato_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping(value = "/{id}/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    public ResponseEntity<ContratoResponse> subirFirmado(
            @PathVariable Long id,
            @Parameter(description = "Archivo firmado (PDF o Imagen)", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(contratoService.subirContratoFirmado(id, file));
    }
}
