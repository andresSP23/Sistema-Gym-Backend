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
            Document doc = new Document(pdf, PageSize.A4);
            doc.setMargins(30, 30, 30, 30);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // ==========
            // HEADER: EMPRESA / SUCURSAL
            // ==========
            String sucursalNombre = venta.getSucursal() != null ? ns(venta.getSucursal().getNombre()) : "-";
            // Ajusta estos getters a tu modelo Sucursal (los nombres pueden variar)
            String sucursalDireccion = venta.getSucursal() != null ? ns(venta.getSucursal().getDireccion()) : "-";
            String sucursalTelefono = venta.getSucursal() != null ? ns(venta.getSucursal().getTelefono()) : "-";
            String sucursalEmail = venta.getSucursal() != null ? ns(venta.getSucursal().getEmail()) : "-";
            String sucursalRuc = venta.getSucursal() != null ? ns(venta.getSucursal().getRuc()) : "-";
            String razonSocial = venta.getSucursal() != null ? ns(venta.getSucursal().getRazonSocial())
                    : sucursalNombre;

            // Título
            doc.add(new Paragraph("FACTURA")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(16));

            // Bloque emisor
            Table emisor = new Table(UnitValue.createPercentArray(new float[] { 70, 30 }))
                    .useAllAvailableWidth();

            emisor.addCell(new Cell().setBorder(null).add(new Paragraph(ns(razonSocial)).setBold()));
            emisor.addCell(new Cell().setBorder(null).add(new Paragraph("N°: " + ns(c.getNumero())).setBold())
                    .setTextAlignment(TextAlignment.RIGHT));

            emisor.addCell(new Cell().setBorder(null).add(new Paragraph("RUC: " + ns(sucursalRuc))));
            emisor.addCell(new Cell().setBorder(null).add(new Paragraph("Fecha: " +
                    (venta.getFechaVenta() != null ? venta.getFechaVenta().format(dtf) : "-")))
                    .setTextAlignment(TextAlignment.RIGHT));

            emisor.addCell(new Cell().setBorder(null).add(new Paragraph("Sucursal: " + ns(sucursalNombre))));
            String cajero = (venta.getCajeroUsuario() != null)
                    ? ns(venta.getCajeroUsuario().getNombre()) + " " + ns(venta.getCajeroUsuario().getApellido())
                    : "-";
            emisor.addCell(new Cell().setBorder(null).add(new Paragraph("Cajero: " + cajero))
                    .setTextAlignment(TextAlignment.RIGHT));

            emisor.addCell(new Cell().setBorder(null).add(new Paragraph("Dirección: " + ns(sucursalDireccion))));
            emisor.addCell(new Cell().setBorder(null).add(new Paragraph("Estado: " +
                    (venta.getEstado() != null ? venta.getEstado().name() : "-")))
                    .setTextAlignment(TextAlignment.RIGHT));

            emisor.addCell(new Cell().setBorder(null)
                    .add(new Paragraph("Tel: " + ns(sucursalTelefono) + " | Email: " + ns(sucursalEmail))));
            emisor.addCell(new Cell().setBorder(null).add(new Paragraph(" ")));

            doc.add(emisor);
            doc.add(new Paragraph(" "));

            // ==========
            // CLIENTE
            // ==========
            String clienteNombre = venta.getCliente() != null
                    ? (ns(venta.getCliente().getNombres()) + " " + ns(venta.getCliente().getApellidos())).trim()
                    : "-";

            // Ajusta estos getters a tu Cliente si existen
            String clienteIdentificacion = venta.getCliente() != null ? ns(venta.getCliente().getCedula()) : "-";
            String clienteTelefono = venta.getCliente() != null ? ns(venta.getCliente().getTelefono()) : "-";
            String clienteEmail = venta.getCliente() != null ? ns(venta.getCliente().getEmail()) : "-";
            String clienteDireccion = venta.getCliente() != null ? ns(venta.getCliente().getDireccion()) : "-";

            Table clienteTbl = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }))
                    .useAllAvailableWidth();

            clienteTbl
                    .addCell(new Cell().setBorder(null).add(new Paragraph("Cliente: " + ns(clienteNombre)).setBold()));
            clienteTbl.addCell(
                    new Cell().setBorder(null).add(new Paragraph("Identificación: " + ns(clienteIdentificacion))));

            clienteTbl.addCell(new Cell().setBorder(null).add(new Paragraph("Teléfono: " + ns(clienteTelefono))));
            clienteTbl.addCell(new Cell().setBorder(null).add(new Paragraph("Email: " + ns(clienteEmail))));

            clienteTbl.addCell(
                    new Cell(1, 2)
                            .setBorder(null)
                            .add(new Paragraph("Dirección: " + ns(clienteDireccion))));

            doc.add(clienteTbl);
            doc.add(new Paragraph(" "));

            // ==========
            // DETALLE
            // ==========
            Table table = new Table(UnitValue.createPercentArray(new float[] { 45, 15, 10, 15, 15 }))
                    .useAllAvailableWidth();

            table.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Tipo").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("P.Unit").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

            venta.getDetalles().forEach(d -> {
                table.addCell(new Cell().add(new Paragraph(ns(d.getDescripcionSnapshot()))));
                table.addCell(new Cell().add(new Paragraph(d.getTipoItem() != null ? d.getTipoItem().name() : "-")));
                table.addCell(
                        new Cell().add(new Paragraph(d.getCantidad() != null ? d.getCantidad().toPlainString() : "0")));
                table.addCell(new Cell().add(new Paragraph(money(d.getPrecioUnitarioSnapshot()))));
                table.addCell(new Cell().add(new Paragraph(money(d.getTotalLinea()))));
            });

            doc.add(table);
            doc.add(new Paragraph(" "));

            // ==========
            // TOTALES
            // ==========
            Table totales = new Table(UnitValue.createPercentArray(new float[] { 70, 30 }))
                    .useAllAvailableWidth();

            totales.addCell(new Cell().setBorder(null).add(new Paragraph(" ")));
            totales.addCell(new Cell().setBorder(null).add(new Paragraph("Subtotal: " + money(venta.getSubtotal())))
                    .setTextAlignment(TextAlignment.RIGHT));

            totales.addCell(new Cell().setBorder(null).add(new Paragraph(" ")));
            totales.addCell(
                    new Cell().setBorder(null).add(new Paragraph("Descuento: " + money(venta.getDescuentoTotal())))
                            .setTextAlignment(TextAlignment.RIGHT));

            totales.addCell(new Cell().setBorder(null).add(new Paragraph(" ")));
            totales.addCell(
                    new Cell().setBorder(null).add(new Paragraph("Impuesto: " + money(venta.getImpuestoTotal())))
                            .setTextAlignment(TextAlignment.RIGHT));

            totales.addCell(new Cell().setBorder(null).add(new Paragraph(" ")));
            totales.addCell(new Cell().setBorder(null).add(new Paragraph("TOTAL: " + money(venta.getTotal()))
                    .setBold().setFontSize(12))
                    .setTextAlignment(TextAlignment.RIGHT));

            doc.add(totales);

            // Footer
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Gracias por su compra.")
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_GENERATION_FAILED);
        }
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
                throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_NOT_FOUND_OR_UNREADABLE);
            }

            String safeName = (c.getNumero() != null && !c.getNumero().isBlank())
                    ? c.getNumero().replaceAll("[^a-zA-Z0-9-_\\.]", "_")
                    : "comprobante-" + comprobanteId;

            String fileName = safeName + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (BussinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_DOWNLOAD_FAILED);
        }
    }
}
