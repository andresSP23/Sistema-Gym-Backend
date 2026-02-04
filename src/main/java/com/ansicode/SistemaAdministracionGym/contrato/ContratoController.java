package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoContrato;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

import java.time.LocalDateTime;

@RestController
@RequestMapping("contratos")
@Tag(name = "Contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService contratoService;

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    @Operation(summary = "Crear contrato", description = "Crea un nuevo contrato para un cliente.")
    @ApiResponse(responseCode = "200", description = "Contrato creado exitosamente")
    public ResponseEntity<ContratoResponse> create(
            @RequestBody @Valid ContratoRequest request) {
        return ResponseEntity.ok(contratoService.create(request));
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    @Operation(summary = "Actualizar contrato", description = "Actualiza un contrato existente.")
    @ApiResponse(responseCode = "200", description = "Contrato actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    public ResponseEntity<ContratoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ContratoRequest request) {
        return ResponseEntity.ok(contratoService.update(id, request));
    }

    @GetMapping("/buscar-por-id/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    @Operation(summary = "Buscar contrato por ID", description = "Obtiene un contrato por su ID único.")
    @ApiResponse(responseCode = "200", description = "Contrato encontrado")
    @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    public ResponseEntity<ContratoResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(contratoService.findById(id));
    }

    /**
     * Lista contratos con filtros y ordenamiento DESC.
     * Orden por defecto: más reciente primero (createdAt DESC, id DESC)
     */
    @GetMapping("/findAll")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    @Operation(summary = "Listar contratos", description = "Lista contratos con filtros opcionales y orden DESC")
    public ResponseEntity<PageResponse<ContratoResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EstadoContrato estado,
            @RequestParam(required = false) String q, // Buscar por cliente: nombre/cedula/email
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));
        return ResponseEntity.ok(contratoService.findAllConFiltros(estado, q, desde, hasta, pageable));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar contrato", description = "Elimina un contrato del sistema.")
    @ApiResponse(responseCode = "204", description = "Contrato eliminado exitosamente")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        contratoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    @Operation(summary = "Generar PDF del contrato", description = "Genera una versión PDF del contrato.")
    @ApiResponse(responseCode = "200", description = "PDF generado exitosamente")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Long id) {
        byte[] pdfBytes = contratoService.generarContratoPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "contrato_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/{id}/preview")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    @Operation(summary = "Vista previa del contrato", description = "Devuelve una vista previa HTML del contrato.")
    @ApiResponse(responseCode = "200", description = "Vista previa generada exitosamente")
    public ResponseEntity<String> preview(@PathVariable Long id) {
        // Obtenemos response normal que ya tiene el contenido
        ContratoResponse response = contratoService.findById(id);

        // Retornamos solo el HTML/Texto
        String html = response.getContenidoContrato();
        if (html == null)
            html = "";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @PostMapping(value = "/{id}/subir-firmado", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CAJERO')")
    @Operation(summary = "Subir contrato firmado", description = "Sube un archivo de contrato firmado (PDF o imagen).")
    @ApiResponse(responseCode = "200", description = "Contrato firmado subido exitosamente")
    public ResponseEntity<ContratoResponse> subirFirmado(
            @PathVariable Long id,
            @Parameter(description = "Archivo firmado (PDF o Imagen)", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(contratoService.subirContratoFirmado(id, file));
    }
}
