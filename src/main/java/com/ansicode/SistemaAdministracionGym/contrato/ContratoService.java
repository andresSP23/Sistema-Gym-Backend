package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoContrato;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final ContratoMapper contratoMapper;
    private final ClienteRepository clienteRepository;
    private final com.ansicode.SistemaAdministracionGym.contrato.plantilla.PlantillaContratoRepository plantillaContratoRepository;
    @org.springframework.beans.factory.annotation.Value("${app.storage.contratos-dir:uploads/contratos/}")
    private String uploadDir;

    @Transactional
    public ContratoResponse create(ContratoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CLIENTE_NOT_FOUND));

        Contrato contrato = contratoMapper.toContrato(request, cliente);
        // contrato.setCliente(cliente); // Ya lo hace el mapper

        if (request.getEstadoContrato() == null) {
            contrato.setEstadoContrato(com.ansicode.SistemaAdministracionGym.enums.EstadoContrato.PENDIENTE);
        } else {
            contrato.setEstadoContrato(request.getEstadoContrato());
        }

        return contratoMapper.toContratoResponse(contratoRepository.save(contrato));
    }

    @Transactional
    public ContratoResponse update(Long id, ContratoRequest request) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CONTRATO_NOT_FOUND));

        if (!contrato.getCliente().getId().equals(request.getClienteId())) {
            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CLIENTE_NOT_FOUND));
            contrato.setCliente(cliente);
        }

        contrato.setArchivoUrl(request.getArchivoUrl());
        contrato.setEstadoContrato(request.getEstadoContrato());

        return contratoMapper.toContratoResponse(contratoRepository.save(contrato));
    }

    public ContratoResponse findById(Long id) {
        return contratoRepository.findById(id)
                .map(contratoMapper::toContratoResponse)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CONTRATO_NOT_FOUND));
    }

    public PageResponse<ContratoResponse> findAll(Pageable pageable) {
        Page<Contrato> page = contratoRepository.findAll(pageable);
        List<ContratoResponse> content = page.getContent().stream()
                .map(contratoMapper::toContratoResponse)
                .toList();

        return PageResponse.<ContratoResponse>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    /**
     * Lista contratos con filtros dinámicos.
     */
    @Transactional(readOnly = true)
    public PageResponse<ContratoResponse> findAllConFiltros(
            EstadoContrato estado,
            String clienteBusqueda,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable) {

        Specification<Contrato> spec = ContratoSpecifications.conFiltros(estado, clienteBusqueda, desde, hasta);
        Page<Contrato> page = contratoRepository.findAll(spec, pageable);

        List<ContratoResponse> content = page.getContent().stream()
                .map(contratoMapper::toContratoResponse)
                .toList();

        return PageResponse.<ContratoResponse>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional
    public void delete(Long id) {
        if (!contratoRepository.existsById(id)) {
            throw new BussinessException(BusinessErrorCodes.CONTRATO_NOT_FOUND);
        }
        contratoRepository.deleteById(id);
    }

    @Transactional
    public Contrato generarContratoAutomatico(
            com.ansicode.SistemaAdministracionGym.clientesuscripcion.ClienteSuscripcion suscripcion) {
        // Buscar plantilla activa
        // Buscar plantilla activa
        com.ansicode.SistemaAdministracionGym.contrato.plantilla.PlantillaContrato plantilla = plantillaContratoRepository
                .findDefaultActive()
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PLANTILLA_CONTRATO_NOT_FOUND));

        // Reemplazar marcadores
        String contenidoFinal = procesarPlantilla(plantilla.getContenido(), suscripcion);

        Contrato contrato = Contrato.builder()
                .cliente(suscripcion.getCliente())
                .suscripcion(suscripcion)
                .contenidoContrato(contenidoFinal)
                .estadoContrato(com.ansicode.SistemaAdministracionGym.enums.EstadoContrato.PENDIENTE)
                .build();

        return contratoRepository.save(contrato);
    }

    private String procesarPlantilla(String contenidoBase,
            com.ansicode.SistemaAdministracionGym.clientesuscripcion.ClienteSuscripcion suscripcion) {
        Cliente cliente = suscripcion.getCliente();
        String texto = contenidoBase
                .replace("{{CLIENTE_NOMBRE}}", cliente.getNombres() + " " + cliente.getApellidos())
                .replace("{{CLIENTE_DNI}}", cliente.getCedula() != null ? cliente.getCedula() : "N/A")
                .replace("{{PLAN_NOMBRE}}", suscripcion.getServicio().getNombre())
                .replace("{{FECHA_INICIO}}", suscripcion.getFechaInicio().toLocalDate().toString())
                .replace("{{FECHA_FIN}}", suscripcion.getFechaFin().toLocalDate().toString())
                .replace("{{PRECIO}}", suscripcion.getServicio().getPrecio().toString());

        return texto;
    }

    public byte[] generarContratoPdf(Long contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CONTRATO_NOT_FOUND));

        String textoContrato = contrato.getContenidoContrato();

        // 1. Si ya tiene archivo firmado subido, retornamos ese archivo
        if (contrato.getArchivoUrl() != null && !contrato.getArchivoUrl().isEmpty()) {
            try {
                Path path = Paths.get(contrato.getArchivoUrl());
                if (Files.exists(path)) {
                    return Files.readAllBytes(path);
                }
            } catch (IOException e) {
                // Log and fallback to generation? Or throw?
                // Fallback to generation seems safer if file is missing but data exists
                e.printStackTrace();
            }
        }

        if (textoContrato == null || textoContrato.isEmpty()) {
            textoContrato = "<p>Contrato Legacy (Sin contenido guardado). Contacte al administrador.</p>";
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            // NO agregamos titulo hardcoded. El titulo debe venir en el HTML (ej:
            // <h1>Titulo</h1>)

            // Renderizar contenido parseando HTML básico
            renderHtmlContent(doc, textoContrato);

            // Espacio antes de firmas
            doc.add(new Paragraph("\n\n\n\n\n"));

            // Firmas
            doc.add(new Paragraph("__________________________             __________________________")
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Firma del Socio                                   Firma del Administrador")
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            // log.error("Error generando PDF", e);
            throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_GENERATION_FAILED);
        }
    }

    /**
     * Parsea HTML básico (p, br, h1-h6, b, strong, i, em) a elementos iText.
     */
    private void renderHtmlContent(Document doc, String html) {
        String procesado = html.replace("<br>", "\n").replace("<br/>", "\n");

        java.util.Stack<com.itextpdf.layout.element.List> listStack = new java.util.Stack<>();
        com.itextpdf.layout.element.Paragraph currentParagraph = new com.itextpdf.layout.element.Paragraph();

        // Estilos por defecto
        boolean isBold = false;
        boolean isItalic = false;
        boolean isUnderline = false;
        boolean isStrike = false;
        float currentFontSize = -1f;
        com.itextpdf.kernel.colors.Color currentColor = null;
        String currentFontFamily = "helvetica"; // Default sans-serif

        int i = 0;
        int len = procesado.length();

        while (i < len) {
            int nextTag = procesado.indexOf('<', i);

            if (nextTag == -1) {
                String text = procesado.substring(i);
                addTextToParagraph(currentParagraph, text, isBold, isItalic, isUnderline, isStrike, currentFontSize,
                        currentColor, currentFontFamily);
                break;
            }

            if (nextTag > i) {
                String text = procesado.substring(i, nextTag);
                addTextToParagraph(currentParagraph, text, isBold, isItalic, isUnderline, isStrike, currentFontSize,
                        currentColor, currentFontFamily);
            }

            int closeTag = procesado.indexOf('>', nextTag);
            if (closeTag == -1)
                break;

            String fullTag = procesado.substring(nextTag + 1, closeTag).toLowerCase();
            boolean isClosing = fullTag.startsWith("/");

            String tagName = isClosing ? fullTag.substring(1) : fullTag;
            String style = "";

            if (tagName.contains(" ")) {
                int spaceIdx = tagName.indexOf(" ");
                style = tagName.substring(spaceIdx + 1);
                tagName = tagName.substring(0, spaceIdx);
            }
            tagName = tagName.trim();

            if (!isClosing && !style.isEmpty()) {
                if (style.contains("color"))
                    currentColor = parseColor(style);
                if (style.contains("font-size"))
                    currentFontSize = parseFontSize(style);
                if (style.contains("font-family"))
                    currentFontFamily = parseFontFamily(style);
            } else if (isClosing && (tagName.equals("span") || tagName.equals("font"))) {
                currentColor = null;
                currentFontSize = -1f;
                currentFontFamily = "helvetica";
            }

            if (tagName.equals("b") || tagName.equals("strong")) {
                isBold = !isClosing;
            } else if (tagName.equals("i") || tagName.equals("em")) {
                isItalic = !isClosing;
            } else if (tagName.equals("u")) {
                isUnderline = !isClosing;
            } else if (tagName.equals("s") || tagName.equals("strike") || tagName.equals("del")) {
                isStrike = !isClosing;
            } else if (tagName.matches("h[1-6]")) {
                // ... (Logic for headers remains mostly same, can rely on implicit font or set
                // bold)
                if (!isClosing) {
                    flushParagraph(doc, listStack, currentParagraph);
                    currentParagraph = new com.itextpdf.layout.element.Paragraph();

                    float size = 18f;
                    if (tagName.equals("h2"))
                        size = 16f;
                    if (tagName.equals("h3"))
                        size = 14f;
                    if (tagName.equals("h4"))
                        size = 12f;
                    currentParagraph.setFontSize(size).setBold();

                    if (style.contains("center"))
                        currentParagraph.setTextAlignment(TextAlignment.CENTER);
                } else {
                    flushParagraph(doc, listStack, currentParagraph);
                    currentParagraph = new com.itextpdf.layout.element.Paragraph();
                }
            } else if (tagName.equals("p") || tagName.equals("div")) {
                if (!isClosing) {
                    if (style.contains("center")) {
                        if (!currentParagraph.isEmpty())
                            flushParagraph(doc, listStack, currentParagraph);
                        currentParagraph = new com.itextpdf.layout.element.Paragraph()
                                .setTextAlignment(TextAlignment.CENTER);
                    } else if (style.contains("right")) {
                        if (!currentParagraph.isEmpty())
                            flushParagraph(doc, listStack, currentParagraph);
                        currentParagraph = new com.itextpdf.layout.element.Paragraph()
                                .setTextAlignment(TextAlignment.RIGHT);
                    } else if (style.contains("justify")) {
                        if (!currentParagraph.isEmpty())
                            flushParagraph(doc, listStack, currentParagraph);
                        currentParagraph = new com.itextpdf.layout.element.Paragraph()
                                .setTextAlignment(TextAlignment.JUSTIFIED);
                    }
                } else {
                    flushParagraph(doc, listStack, currentParagraph);
                    currentParagraph = new com.itextpdf.layout.element.Paragraph();
                }
            } else if (tagName.equals("br")) {
                currentParagraph.add(new com.itextpdf.layout.element.Text("\n"));
            } else if (tagName.equals("ul")) {
                if (!isClosing) {
                    flushParagraph(doc, listStack, currentParagraph);
                    currentParagraph = new com.itextpdf.layout.element.Paragraph();
                    com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
                    list.setListSymbol("\u2022");
                    listStack.push(list);
                } else {
                    if (!listStack.isEmpty()) {
                        com.itextpdf.layout.element.List list = listStack.pop();
                        if (!listStack.isEmpty()) {
                            com.itextpdf.layout.element.ListItem item = new com.itextpdf.layout.element.ListItem();
                            item.add(list);
                            listStack.peek().add(item);
                        } else {
                            doc.add(list);
                        }
                    }
                }
            } else if (tagName.equals("ol")) {
                if (!isClosing) {
                    flushParagraph(doc, listStack, currentParagraph);
                    currentParagraph = new com.itextpdf.layout.element.Paragraph();
                    com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List(
                            com.itextpdf.layout.properties.ListNumberingType.DECIMAL);
                    listStack.push(list);
                } else {
                    if (!listStack.isEmpty()) {
                        com.itextpdf.layout.element.List list = listStack.pop();
                        if (!listStack.isEmpty()) {
                            com.itextpdf.layout.element.ListItem item = new com.itextpdf.layout.element.ListItem();
                            item.add(list);
                            listStack.peek().add(item);
                        } else {
                            doc.add(list);
                        }
                    }
                }
            } else if (tagName.equals("li")) {
                if (!isClosing) {
                    if (!listStack.isEmpty()) {
                        currentParagraph = new com.itextpdf.layout.element.Paragraph();
                    }
                } else {
                    if (!listStack.isEmpty()) {
                        com.itextpdf.layout.element.ListItem item = new com.itextpdf.layout.element.ListItem();
                        item.add(currentParagraph);
                        listStack.peek().add(item);
                        currentParagraph = new com.itextpdf.layout.element.Paragraph();
                    }
                }
            }

            i = closeTag + 1;
        }

        flushParagraph(doc, listStack, currentParagraph);
    }

    private String parseFontFamily(String style) {
        try {
            int idx = style.indexOf("font-family:");
            if (idx == -1)
                return "helvetica";
            String val = style.substring(idx + 12).trim();
            int end = val.indexOf(";");
            if (end != -1)
                val = val.substring(0, end).trim();
            val = val.replace("'", "").replace("\"", "").toLowerCase();

            if (val.contains("serif") && !val.contains("sans-serif"))
                return "times";
            if (val.contains("monospace") || val.contains("courier"))
                return "courier";
            return "helvetica";
        } catch (Exception e) {
            return "helvetica";
        }
    }

    private com.itextpdf.kernel.colors.Color parseColor(String style) {
        try {
            int idx = style.indexOf("color:");
            if (idx == -1)
                return null;
            String val = style.substring(idx + 6).trim();
            int end = val.indexOf(";");
            if (end != -1)
                val = val.substring(0, end).trim();

            // Clean up quotes and extra spaces
            val = val.replace("\"", "").replace("'", "").trim();
            if (val.contains(" "))
                val = val.split(" ")[0];

            if (val.startsWith("#") && val.length() == 7) {
                int r = Integer.valueOf(val.substring(1, 3), 16);
                int g = Integer.valueOf(val.substring(3, 5), 16);
                int b = Integer.valueOf(val.substring(5, 7), 16);
                return new com.itextpdf.kernel.colors.DeviceRgb(r, g, b);
            } else if (val.startsWith("rgb")) {
                // Parse rgb(r, g, b) or rgb(r,g,b)
                try {
                    String clean = val.replace("rgb(", "").replace(")", "").trim();
                    String[] parts = clean.split(",");
                    if (parts.length == 3) {
                        int r = Integer.parseInt(parts[0].trim());
                        int g = Integer.parseInt(parts[1].trim());
                        int b = Integer.parseInt(parts[2].trim());
                        return new com.itextpdf.kernel.colors.DeviceRgb(r, g, b);
                    }
                } catch (Exception ex) {
                }
            } else {
                switch (val.toLowerCase()) {
                    case "red":
                        return com.itextpdf.kernel.colors.ColorConstants.RED;
                    case "blue":
                        return com.itextpdf.kernel.colors.ColorConstants.BLUE;
                    case "green":
                        return com.itextpdf.kernel.colors.ColorConstants.GREEN;
                    case "orange":
                        return com.itextpdf.kernel.colors.ColorConstants.ORANGE;
                    case "gray":
                        return com.itextpdf.kernel.colors.ColorConstants.GRAY;
                    case "black":
                        return com.itextpdf.kernel.colors.ColorConstants.BLACK;
                    case "white":
                        return com.itextpdf.kernel.colors.ColorConstants.WHITE;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private float parseFontSize(String style) {
        try {
            int idx = style.indexOf("font-size:");
            if (idx == -1)
                return -1f;
            String val = style.substring(idx + 10).trim();
            int end = val.indexOf(";");
            if (end != -1)
                val = val.substring(0, end).trim();
            val = val.replace("px", "").replace("pt", "").trim();
            return Float.parseFloat(val);
        } catch (Exception e) {
            return -1f;
        }
    }

    private void flushParagraph(Document doc, java.util.Stack<com.itextpdf.layout.element.List> listStack,
            com.itextpdf.layout.element.Paragraph p) {
        if (p.isEmpty())
            return;
        if (listStack.isEmpty()) {
            doc.add(p);
        }
    }

    private void addTextToParagraph(com.itextpdf.layout.element.Paragraph p, String content,
            boolean bold, boolean italic, boolean underline, boolean strike, float fontSize,
            com.itextpdf.kernel.colors.Color color, String fontFamily) {
        if (content.isEmpty())
            return;
        content = content.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&")
                .replace("&quot;", "\"");

        com.itextpdf.layout.element.Text t = new com.itextpdf.layout.element.Text(content);

        // Aplicar fuente nativa
        boolean fontApplied = false;
        try {
            com.itextpdf.kernel.font.PdfFont font = resolveFont(fontFamily, bold, italic);
            if (font != null) {
                t.setFont(font);
                fontApplied = true;
            }
        } catch (Exception e) {
        }

        // Fallbacks si la fuente falló o para estilos extra
        if (!fontApplied) {
            if (bold)
                t.setBold();
            if (italic)
                t.setItalic();
        }

        if (underline)
            t.setUnderline();
        if (strike)
            t.setLineThrough();
        if (fontSize > 0)
            t.setFontSize(fontSize);
        if (color != null)
            t.setFontColor(color);
        p.add(t);
    }

    private com.itextpdf.kernel.font.PdfFont resolveFont(String family, boolean bold, boolean italic)
            throws java.io.IOException {
        // Mapeo a StandardFonts
        String fontName = com.itextpdf.io.font.constants.StandardFonts.HELVETICA;

        if (family.equals("times")) {
            if (bold && italic)
                fontName = com.itextpdf.io.font.constants.StandardFonts.TIMES_BOLDITALIC;
            else if (bold)
                fontName = com.itextpdf.io.font.constants.StandardFonts.TIMES_BOLD;
            else if (italic)
                fontName = com.itextpdf.io.font.constants.StandardFonts.TIMES_ITALIC;
            else
                fontName = com.itextpdf.io.font.constants.StandardFonts.TIMES_ROMAN;
        } else if (family.equals("courier")) {
            if (bold && italic)
                fontName = com.itextpdf.io.font.constants.StandardFonts.COURIER_BOLDOBLIQUE;
            else if (bold)
                fontName = com.itextpdf.io.font.constants.StandardFonts.COURIER_BOLD;
            else if (italic)
                fontName = com.itextpdf.io.font.constants.StandardFonts.COURIER_OBLIQUE;
            else
                fontName = com.itextpdf.io.font.constants.StandardFonts.COURIER;
        } else {
            // Helvetica (default)
            if (bold && italic)
                fontName = com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLDOBLIQUE;
            else if (bold)
                fontName = com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD;
            else if (italic)
                fontName = com.itextpdf.io.font.constants.StandardFonts.HELVETICA_OBLIQUE;
            else
                fontName = com.itextpdf.io.font.constants.StandardFonts.HELVETICA;
        }

        return com.itextpdf.kernel.font.PdfFontFactory.createFont(fontName);
    }

    @Transactional
    public ContratoResponse subirContratoFirmado(Long contratoId, MultipartFile file) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CONTRATO_NOT_FOUND));

        try {
            if (file.isEmpty()) {
                throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
            }

            // 1. Validar nombre y extensión (Seguridad Path Traversal)
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.contains("..")) {
                throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR); // Filename invalido
            }

            String ext = "";
            int i = originalFilename.lastIndexOf('.');
            if (i > 0) {
                ext = originalFilename.substring(i + 1).toLowerCase();
            }

            if (!List.of("pdf", "jpg", "jpeg", "png").contains(ext)) {
                throw new RuntimeException("Formato de archivo no permitido. Solo PDF o Imagenes.");
            }

            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único seguro
            String safeFileName = UUID.randomUUID().toString() + "." + ext;
            Path filePath = uploadPath.resolve(safeFileName);

            // Guardar archivo
            Files.copy(file.getInputStream(), filePath);

            // Actualizar entidad
            contrato.setArchivoUrl(filePath.toString());
            contrato.setEstadoContrato(com.ansicode.SistemaAdministracionGym.enums.EstadoContrato.ACTIVO);
            contrato.setFechaFirma(java.time.LocalDateTime.now());

            return contratoMapper.toContratoResponse(contratoRepository.save(contrato));

        } catch (IOException e) {
            // log.error("Error al subir contrato", e); // Si tuviera Slf4j, por ahora
            // runtime
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }
}
