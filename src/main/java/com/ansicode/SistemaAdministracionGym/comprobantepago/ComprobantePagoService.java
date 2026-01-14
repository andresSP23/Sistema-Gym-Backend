package com.ansicode.SistemaAdministracionGym.comprobantepago;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import com.ansicode.SistemaAdministracionGym.pago.PagoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ComprobantePagoService {

    private final PagoRepository pagoRepository;
    private final ComprobantePagoRepository comprobantePagoRepository;
    private final ComprobantePagoMapper mapper;

    @Transactional
    public ComprobantePagoResponse generarYGuardarComprobante(Long pagoId) {

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pago no encontrado")
                );

        //  Armar response base (para JSON)
        ComprobantePagoResponse comprobanteResponse = mapper.toResponse(
                construirEntidadTemporal(pago)
        );

        // ⃣ Generar PDF
        byte[] pdfBytes = generarPdfBytes(pago);

        // Guardar comprobante
        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setPago(pago);
        comprobante.setContenido(convertToJson(comprobanteResponse));
        comprobante.setPdfData(pdfBytes);
        comprobante.setFechaGeneracion(LocalDateTime.now());
        comprobante.setActivo(true);

        comprobantePagoRepository.save(comprobante);

        return comprobanteResponse;
    }


    @Transactional(readOnly = true)
    public List<ComprobantePagoResponse> listarComprobantesPorCliente(Long clienteId) {

        List<ComprobantePago> comprobantes =
                comprobantePagoRepository.findByPagoId(clienteId);

        return comprobantes.stream()
                .map(c -> convertFromJson(c.getContenido()))
                .toList();
    }


    @Transactional(readOnly = true)
    public byte[] descargarPdf(Long comprobanteId) {

        ComprobantePago comprobante = comprobantePagoRepository.findById(comprobanteId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Comprobante no encontrado")
                );

        if (comprobante.getPdfData() == null) {
            throw new RuntimeException("PDF no generado para este comprobante");
        }

        return comprobante.getPdfData();
    }


    private byte[] generarPdfBytes(Pago pago) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document document = new Document(pdfDoc);

            Cliente cliente = pago.getMembresiaCliente().getCliente();

            document.add(new Paragraph("COMPROBANTE DE PAGO")
                    .setBold()
                    .setFontSize(14));

            document.add(new Paragraph("Cliente: " + cliente.getNombreCompleto()));
            document.add(new Paragraph("Membresía: " +
                    pago.getMembresiaCliente().getMembresia().getNombre()));
            document.add(new Paragraph("Fecha de pago: " + pago.getFechaPago()));
            document.add(new Paragraph("Método de pago: " + pago.getMetodoPago()));
            document.add(new Paragraph("Estado del pago: " + pago.getEstadoPago()));

            document.add(new Paragraph(" "));

            document.add(new Paragraph("MONTO PAGADO: $" + pago.getMonto())
                    .setBold());

            document.close();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF del comprobante de pago", e);
        }
    }


    private String convertToJson(Object object) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir a JSON", e);
        }
    }

    private ComprobantePagoResponse convertFromJson(String json) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            return mapper.readValue(json, ComprobantePagoResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir JSON a DTO", e);
        }
    }

    /**
     * Entidad temporal solo para reutilizar el mapper
     */
    private ComprobantePago construirEntidadTemporal(Pago pago) {

        ComprobantePago temp = new ComprobantePago();
        temp.setPago(pago);
        temp.setFechaGeneracion(LocalDateTime.now());
        temp.setActivo(true);
        temp.setContenido(""); // se reemplaza luego

        return temp;
    }


}