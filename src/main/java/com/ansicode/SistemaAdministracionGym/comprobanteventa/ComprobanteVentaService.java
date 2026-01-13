package com.ansicode.SistemaAdministracionGym.comprobanteventa;

import com.ansicode.SistemaAdministracionGym.sucursal.Sucursal;
import com.ansicode.SistemaAdministracionGym.sucursal.SucursalRepository;
import com.ansicode.SistemaAdministracionGym.venta.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ComprobanteVentaService {

    private final ComprobanteVentaRepository comprobanteVentaRepository;
    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final ComprobanteVentaMapper mapper;
    private final VentaMapper ventaMapper;




    @Transactional
    public ComprobanteVentaResponse generarYGuardarComprobante(Long ventaId, List<DetalleVentaResponse> detalles) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));

        VentaResponse ventaResponse = ventaMapper.toVentaResponse(venta, detalles);

        Sucursal sucursal = sucursalRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay sucursal registrada"));

        ComprobanteVentaResponse comprobanteResponse = mapper.fromVentaResponse(ventaResponse, sucursal);

        byte[] pdfBytes = generarPdfBytes(ventaResponse, sucursal);

        ComprobanteVenta comprobante = new ComprobanteVenta();
        comprobante.setVenta(venta);
        comprobante.setContenido(convertToJson(comprobanteResponse));
        comprobante.setPdfData(pdfBytes);
        comprobante.setFechaGeneracion(LocalDateTime.now());
        comprobante.setActivo(true);

        comprobanteVentaRepository.save(comprobante);

        return comprobanteResponse;
    }


    @Transactional(readOnly = true)
    public List<ComprobanteVentaResponse> listarComprobantesPorCliente(Long clienteId) {
        List<ComprobanteVenta> comprobantes = comprobanteVentaRepository.findByVentaClienteId(clienteId);

        return comprobantes.stream()
                .map(c -> convertFromJson(c.getContenido()))
                .toList();
    }

    @Transactional(readOnly = true)
    public byte[] descargarPdf(Long comprobanteId) {
        ComprobanteVenta comprobante = comprobanteVentaRepository.findById(comprobanteId)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado"));

        if (comprobante.getPdfData() == null) {
            throw new RuntimeException("PDF no generado para este comprobante");
        }
        return comprobante.getPdfData();
    }

    // Genera PDF en bytes desde la venta y sucursal
    private byte[] generarPdfBytes(VentaResponse ventaResponse, Sucursal sucursal) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("COMPROBANTE DE VENTA").setBold().setFontSize(14));
            document.add(new Paragraph("Sucursal: " + sucursal.getNombre()));
            document.add(new Paragraph("Fecha: " + ventaResponse.getFechaVenta()));
            document.add(new Paragraph("Cliente: " + ventaResponse.getClienteNombre()));
            document.add(new Paragraph("Vendedor: " + ventaResponse.getVendedorNombre()));
            document.add(new Paragraph("Método de Pago: " + ventaResponse.getMetodoPago()));

            float[] columnWidths = {4, 2, 2, 2};
            Table table = new Table(columnWidths);
            table.addHeaderCell("Producto");
            table.addHeaderCell("Cantidad");
            table.addHeaderCell("Precio Unitario");
            table.addHeaderCell("Subtotal");

            for (DetalleVentaResponse d : ventaResponse.getDetalles()) {
                table.addCell(d.getProductoNombre());
                table.addCell(String.valueOf(d.getCantidad()));
                table.addCell(d.getPrecioUnitario().toString());
                table.addCell(d.getSubtotal().toString());
            }

            document.add(table);
            document.add(new Paragraph("TOTAL: " + ventaResponse.getTotal()).setBold());

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF", e);
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

    private ComprobanteVentaResponse convertFromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            return mapper.readValue(json, ComprobanteVentaResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir JSON a DTO", e);
        }
    }






}
