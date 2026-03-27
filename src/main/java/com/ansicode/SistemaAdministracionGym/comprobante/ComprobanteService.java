package com.ansicode.SistemaAdministracionGym.comprobante;

import com.ansicode.SistemaAdministracionGym.enums.EstadoComprobante;
import com.ansicode.SistemaAdministracionGym.enums.TipoComprobante;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.math.BigDecimal;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ComprobanteService {

        private final ComprobanteRepository comprobanteRepository;

        @Value("${app.storage.comprobantes-dir:storage/comprobantes}")
        private String comprobantesDir;

        @Transactional
        public Comprobante generarFacturaPdf(Venta venta) {

                if (venta == null) {
                        throw new BussinessException(BusinessErrorCodes.COMPROBANTE_VENTA_REQUIRED);
                }

                if (venta.getNumeroFactura() == null || venta.getNumeroFactura().isBlank()) {
                        throw new BussinessException(BusinessErrorCodes.COMPROBANTE_NUMERO_FACTURA_REQUIRED);
                }

                if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
                        throw new BussinessException(BusinessErrorCodes.COMPROBANTE_VENTA_SIN_DETALLES);
                }

                Comprobante c = new Comprobante();
                c.setVenta(venta);
                c.setTipo(TipoComprobante.FACTURA);
                c.setEstado(EstadoComprobante.GENERADO);
                c.setNumero(venta.getNumeroFactura());

                // 1) Guardar primero
                c = comprobanteRepository.save(c);

                // 2) Generar bytes PDF
                byte[] pdfBytes = generarPdfBytes(c);

                // 3) Guardar en disco y setear pdfRef
                String pdfRef = guardarPdfEnDisco(c.getNumero(), pdfBytes);
                c.setPdfRef(pdfRef);

                return comprobanteRepository.save(c);
        }

        private byte[] generarPdfBytes(Comprobante c) {
                Venta venta = c.getVenta();

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                        PdfWriter writer = new PdfWriter(baos);
                        PdfDocument pdf = new PdfDocument(writer);
                        // Márgenes reducidos para diseño moderno
                        Document doc = new Document(pdf, PageSize.A4);
                        doc.setMargins(20, 20, 20, 20);

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        // ============================================
                        // 1. LOGO & HEADER (2 Columnas)
                        // ============================================
                        // Load Logo
                        com.itextpdf.layout.element.Image logoImage = null;
                        try {
                                // Intenta cargar desde classpath
                                Path logoPath = Paths.get("src/main/resources/images/logo.png");
                                if (Files.exists(logoPath)) {
                                        com.itextpdf.io.image.ImageData data = com.itextpdf.io.image.ImageDataFactory
                                                        .create(logoPath.toString());
                                        logoImage = new com.itextpdf.layout.element.Image(data);
                                        logoImage.scaleToFit(100, 100);
                                }
                        } catch (Exception e) {
                                // Ignore missing logo
                        }

                        Table headerTbl = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }))
                                        .useAllAvailableWidth();

                        // Columna 1: Logo + Info Empresa
                        Cell colLeft = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
                        if (logoImage != null) {
                                colLeft.add(logoImage);
                        } else {
                                colLeft.add(new Paragraph("GYM FLOW").setBold().setFontSize(20));
                        }

                        String sucursalNombre = venta.getSucursal() != null ? ns(venta.getSucursal().getNombre())
                                        : "Matriz";
                        String sucursalDir = venta.getSucursal() != null ? ns(venta.getSucursal().getDireccion()) : "-";
                        String sucursalTel = venta.getSucursal() != null ? ns(venta.getSucursal().getTelefono()) : "-";
                        String sucursalRuc = venta.getSucursal() != null ? ns(venta.getSucursal().getRuc()) : "-";

                        colLeft.add(new Paragraph(sucursalNombre).setBold().setFontSize(12));
                        colLeft.add(new Paragraph("RUC: " + sucursalRuc).setFontSize(9));
                        colLeft.add(new Paragraph(sucursalDir).setFontSize(9));
                        colLeft.add(new Paragraph("Tel: " + sucursalTel).setFontSize(9));

                        headerTbl.addCell(colLeft);

                        // Columna 2: Datos Factura (Alineado Derecha, En caja gris opcional o limpio)
                        Cell colRight = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                        .setTextAlignment(TextAlignment.RIGHT);

                        colRight.add(new Paragraph("FACTURA").setBold().setFontSize(18)
                                        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY));
                        colRight.add(new Paragraph("N° " + ns(c.getNumero())).setBold().setFontSize(14));
                        colRight.add(new Paragraph("Fecha: "
                                        + (venta.getFechaVenta() != null ? venta.getFechaVenta().format(dtf) : "-"))
                                        .setFontSize(10));

                        String estadoStr = venta.getEstado() != null ? venta.getEstado().name() : "-";
                        colRight.add(new Paragraph("Estado: " + estadoStr).setFontSize(10));

                        if (venta.getCajeroUsuario() != null) {
                                String cajero = ns(venta.getCajeroUsuario().getNombre()) + " "
                                                + ns(venta.getCajeroUsuario().getApellido());
                                colRight.add(new Paragraph("Atendido por: " + cajero).setFontSize(9));
                        }

                        headerTbl.addCell(colRight);
                        doc.add(headerTbl);

                        // Separador Elegante
                        com.itextpdf.layout.element.LineSeparator line = new com.itextpdf.layout.element.LineSeparator(
                                        new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f));
                        line.setMarginTop(10);
                        line.setMarginBottom(10);
                        doc.add(line);

                        // ============================================
                        // 2. DATOS DEL CLIENTE
                        // ============================================
                        String clienteNombre = venta.getCliente() != null
                                        ? (ns(venta.getCliente().getNombres()) + " "
                                                        + ns(venta.getCliente().getApellidos())).trim()
                                        : "Consumidor Final";
                        String clienteDoc = venta.getCliente() != null ? ns(venta.getCliente().getCedula()) : "-";
                        String clienteTel = venta.getCliente() != null ? ns(venta.getCliente().getTelefono()) : "-";
                        String clienteDir = venta.getCliente() != null ? ns(venta.getCliente().getDireccion()) : "-";

                        Table clienteTbl = new Table(UnitValue.createPercentArray(new float[] { 15, 85 }))
                                        .useAllAvailableWidth();
                        clienteTbl.setMarginBottom(15);

                        clienteTbl.addCell(new Cell().add(new Paragraph("Cliente:").setBold())
                                        .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                        clienteTbl.addCell(new Cell().add(new Paragraph(clienteNombre))
                                        .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));

                        clienteTbl.addCell(new Cell().add(new Paragraph("RUC/Ced:").setBold())
                                        .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                        clienteTbl.addCell(new Cell().add(new Paragraph(clienteDoc))
                                        .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));

                        clienteTbl.addCell(new Cell().add(new Paragraph("Dirección:").setBold())
                                        .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                        clienteTbl.addCell(new Cell().add(new Paragraph(clienteDir + " | Tel: " + clienteTel))
                                        .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));

                        doc.add(clienteTbl);

                        // ============================================
                        // 3. TABLA DE DETALLES
                        // ============================================
                        Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 15, 15, 20 }))
                                        .useAllAvailableWidth();

                        // HEADER COLOREADO (Negro con texto blanco)
                        com.itextpdf.kernel.colors.Color headerBg = com.itextpdf.kernel.colors.ColorConstants.BLACK;
                        com.itextpdf.kernel.colors.Color headerFg = com.itextpdf.kernel.colors.ColorConstants.WHITE;

                        table.addHeaderCell(
                                        new Cell().add(new Paragraph("Descripción").setBold().setFontColor(headerFg))
                                                        .setBackgroundColor(headerBg));
                        table.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold().setFontColor(headerFg))
                                        .setTextAlignment(TextAlignment.CENTER).setBackgroundColor(headerBg));
                        table.addHeaderCell(new Cell().add(new Paragraph("P. Unit").setBold().setFontColor(headerFg))
                                        .setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(headerBg));
                        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setFontColor(headerFg))
                                        .setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(headerBg));

                        // FILAS (Zebra Striping manual)
                        boolean isOdd = true;
                        com.itextpdf.kernel.colors.Color lightGray = new com.itextpdf.kernel.colors.DeviceGray(0.95f);

                        for (com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta d : venta.getDetalles()) {
                                com.itextpdf.kernel.colors.Color rowColor = isOdd
                                                ? com.itextpdf.kernel.colors.ColorConstants.WHITE
                                                : lightGray;

                                table.addCell(new Cell().add(new Paragraph(ns(d.getDescripcionSnapshot())))
                                                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                                .setBackgroundColor(rowColor));

                                String cantStr = d.getCantidad() != null ? d.getCantidad().toPlainString() : "0";
                                table.addCell(new Cell().add(new Paragraph(cantStr))
                                                .setTextAlignment(TextAlignment.CENTER)
                                                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                                .setBackgroundColor(rowColor));

                                table.addCell(new Cell().add(new Paragraph(money(d.getPrecioUnitarioSnapshot())))
                                                .setTextAlignment(TextAlignment.RIGHT)
                                                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                                .setBackgroundColor(rowColor));

                                table.addCell(new Cell().add(new Paragraph(money(d.getTotalLinea())))
                                                .setTextAlignment(TextAlignment.RIGHT)
                                                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                                .setBackgroundColor(rowColor));

                                isOdd = !isOdd;
                        }

                        // Línea final de la tabla
                        table.setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1));
                        doc.add(table);
                        doc.add(new Paragraph(" "));

                        // ============================================
                        // 4. TOTALES & FOOTER
                        // ============================================
                        Table totales = new Table(UnitValue.createPercentArray(new float[] { 70, 30 }))
                                        .useAllAvailableWidth();

                        totales.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                        totales.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                        .add(new Paragraph("Subtotal:   " + money(venta.getSubtotal()))
                                                        .setTextAlignment(TextAlignment.RIGHT)));

                        totales.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                        totales.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                        .add(new Paragraph("Descuento: -" + money(venta.getDescuentoTotal()))
                                                        .setTextAlignment(TextAlignment.RIGHT)));

                        totales.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                        totales.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                        .add(new Paragraph("TOTAL:      $" + money(venta.getTotal()))
                                                        .setBold().setFontSize(14)
                                                        .setTextAlignment(TextAlignment.RIGHT)));

                        doc.add(totales);

                        // FOOTER - LEGAL
                        doc.add(new Paragraph("\n\n"));
                        div(doc); // Line splitter code if needed or just styling

                        Paragraph footer = new Paragraph(
                                        "Términos: Documento válido como comprobante de pago. No se aceptan devoluciones pasados 30 días.\nGracias por entrenar con nosotros en GYM FLOW.")
                                        .setFontSize(8)
                                        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY)
                                        .setTextAlignment(TextAlignment.CENTER);
                        doc.add(footer);

                        doc.close();
                        return baos.toByteArray();

                } catch (Exception e) {
                        e.printStackTrace(); // Log para debug local
                        throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_GENERATION_FAILED);
                }
        }

        private void div(Document doc) {
                // Helper simple para linea gris
                com.itextpdf.layout.element.LineSeparator line = new com.itextpdf.layout.element.LineSeparator(
                                new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(0.5f));
                line.setMarginTop(5);
                doc.add(line);
        }

        private String guardarPdfEnDisco(String numeroComprobante, byte[] pdfBytes) {
                try {
                        Path dir = Paths.get(comprobantesDir);
                        Files.createDirectories(dir);

                        String safe = numeroComprobante.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
                        Path path = dir.resolve(safe + ".pdf");

                        Files.write(path, pdfBytes);

                        return path.toString();
                } catch (Exception e) {
                        throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_SAVE_FAILED);
                }
        }

        private static String money(BigDecimal v) {
                if (v == null)
                        return "0.00";
                return v.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
        }

        private static String ns(String s) {
                return s == null ? "" : s;
        }

        @Transactional(readOnly = true)
        public ResponseEntity<Resource> descargarPdf(Long comprobanteId) {

                Comprobante c = comprobanteRepository.findById(comprobanteId)
                                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.COMPROBANTE_NOT_FOUND));

                if (c.getPdfRef() == null || c.getPdfRef().isBlank()) {
                        throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_NOT_GENERATED);
                }

                try {
                        Path path = Paths.get(c.getPdfRef()).normalize();
                        Resource resource = new UrlResource(path.toUri());

                        if (!resource.exists() || !resource.isReadable()) {
                                throw new BussinessException(
                                                BusinessErrorCodes.COMPROBANTE_PDF_NOT_FOUND_OR_UNREADABLE);
                        }

                        String safeName = (c.getNumero() != null && !c.getNumero().isBlank())
                                        ? c.getNumero().replaceAll("[^a-zA-Z0-9-_\\.]", "_")
                                        : "comprobante-" + comprobanteId;

                        String fileName = safeName + ".pdf";

                        return ResponseEntity.ok()
                                        .contentType(MediaType.APPLICATION_PDF)
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"" + fileName + "\"")
                                        .body(resource);

                } catch (BussinessException be) {
                        throw be;
                } catch (Exception e) {
                        throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_DOWNLOAD_FAILED);
                }
        }
}
