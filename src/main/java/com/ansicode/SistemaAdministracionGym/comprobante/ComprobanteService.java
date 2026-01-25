package com.ansicode.SistemaAdministracionGym.comprobante;

import com.ansicode.SistemaAdministracionGym.enums.EstadoComprobante;
import com.ansicode.SistemaAdministracionGym.enums.TipoComprobante;
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

        if (venta == null) throw new IllegalArgumentException("venta es obligatoria");
        if (venta.getNumeroFactura() == null || venta.getNumeroFactura().isBlank()) {
            throw new IllegalArgumentException("La venta no tiene numeroFactura");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("No se puede generar comprobante: la venta no tiene detalles");
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
            Document doc = new Document(pdf, PageSize.A4);
            doc.setMargins(30, 30, 30, 30);

            doc.add(new Paragraph("FACTURA")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(16));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            doc.add(new Paragraph("Número: " + c.getNumero()).setBold());
            doc.add(new Paragraph("Fecha: " + (venta.getFechaVenta() != null ? venta.getFechaVenta().format(dtf) : "-")));

            doc.add(new Paragraph(" "));

            Table table = new Table(UnitValue.createPercentArray(new float[]{45, 15, 15, 12.5f, 12.5f}))
                    .useAllAvailableWidth();

            table.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Tipo").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Cantidad").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("P.Unit").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

            venta.getDetalles().forEach(d -> {
                table.addCell(new Cell().add(new Paragraph(ns(d.getDescripcionSnapshot()))));
                table.addCell(new Cell().add(new Paragraph(d.getTipoItem() != null ? d.getTipoItem().name() : "-")));
                table.addCell(new Cell().add(new Paragraph(d.getCantidad() != null ? d.getCantidad().toPlainString() : "0")));
                table.addCell(new Cell().add(new Paragraph(money(d.getPrecioUnitarioSnapshot()))));
                table.addCell(new Cell().add(new Paragraph(money(d.getTotalLinea()))));
            });

            doc.add(table);
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Subtotal: " + money(venta.getSubtotal())));
            doc.add(new Paragraph("Descuento: " + money(venta.getDescuentoTotal())));
            doc.add(new Paragraph("Impuesto: " + money(venta.getImpuestoTotal())));
            doc.add(new Paragraph("TOTAL: " + money(venta.getTotal())).setBold().setFontSize(12));

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el PDF", e);
        }
    }

    private String guardarPdfEnDisco(String numeroComprobante, byte[] pdfBytes) {
        try {
            Path dir = Paths.get(comprobantesDir);
            Files.createDirectories(dir);

            String safe = numeroComprobante.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
            Path path = dir.resolve(safe + ".pdf");

            Files.write(path, pdfBytes);

            // pdfRef queda como ruta relativa/real (tu decides)
            return path.toString();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo guardar el PDF en disco", e);
        }
    }

    private static String money(BigDecimal v) {
        if (v == null) return "0.00";
        return v.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
    private static String ns(String s) { return s == null ? "" : s; }




    @Transactional(readOnly = true)
    public ResponseEntity<Resource> descargarPdf(Long comprobanteId) {

        Comprobante c = comprobanteRepository.findById(comprobanteId)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado"));

        if (c.getPdfRef() == null || c.getPdfRef().isBlank()) {
            throw new IllegalArgumentException("Este comprobante no tiene PDF generado");
        }

        try {
            Path path = Paths.get(c.getPdfRef()).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("El archivo PDF no existe o no se puede leer");
            }

            String safeName = (c.getNumero() != null && !c.getNumero().isBlank())
                    ? c.getNumero().replaceAll("[^a-zA-Z0-9-_\\.]", "_")
                    : "comprobante-" + comprobanteId;

            String fileName = safeName + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    // inline = abre en el navegador, attachment = descarga
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo descargar el PDF", e);
        }
    }
}
