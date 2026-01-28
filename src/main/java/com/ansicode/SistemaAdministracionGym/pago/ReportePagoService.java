package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.itextpdf.io.source.ByteArrayOutputStream;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



@Service
@RequiredArgsConstructor
public class ReportePagoService {

    private final PagoRepository pagoRepository;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> exportar(
            LocalDateTime desde,
            LocalDateTime hasta,
            TipoOperacionPago tipoOperacion,
            MetodoPago metodo,
            EstadoPago estado
    ) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BussinessException(BusinessErrorCodes.REPORTE_PAGOS_RANGO_FECHAS_INVALIDO);
        }

        List<Pago> pagos = pagoRepository.buscarPagosReporte(desde, hasta, tipoOperacion, metodo, estado);

        byte[] bytes = generarExcel(pagos, desde, hasta, tipoOperacion, metodo, estado);

        String fileName = "reporte_pagos_" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(new ByteArrayResource(bytes));
    }

    private byte[] generarExcel(
            List<Pago> pagos,
            LocalDateTime desde,
            LocalDateTime hasta,
            TipoOperacionPago tipoOperacion,
            MetodoPago metodo,
            EstadoPago estado
    ) {

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ===== Styles =====
            CellStyle titleStyle = createTitleStyle(wb);
            CellStyle subtitleStyle = createSubtitleStyle(wb);
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle textStyle = createTextStyle(wb);
            CellStyle dateStyle = createDateStyle(wb);
            CellStyle moneyStyle = createMoneyStyle(wb);
            CellStyle intStyle = createIntStyle(wb);
            CellStyle totalStyle = createTotalStyle(wb);

            // ===== Sheets =====
            Sheet resumen = wb.createSheet("Resumen");
            Sheet detalle = wb.createSheet("Detalle");

            // ===== Hoja Resumen =====
            buildResumenSheet(resumen, pagos, desde, hasta, tipoOperacion, metodo, estado,
                    titleStyle, subtitleStyle, headerStyle, textStyle, moneyStyle, intStyle, totalStyle);

            // ===== Hoja Detalle =====
            buildDetalleSheet(detalle, pagos, titleStyle, headerStyle, textStyle, dateStyle, moneyStyle);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            // ✅ ahora sí va por BusinessErrorCodes
            throw new BussinessException(BusinessErrorCodes.REPORTE_PAGOS_EXCEL_ERROR);
        }
    }

    // ===================== Resumen Sheet =====================

    private void buildResumenSheet(
            Sheet sheet,
            List<Pago> pagos,
            LocalDateTime desde,
            LocalDateTime hasta,
            TipoOperacionPago tipoOperacion,
            MetodoPago metodo,
            EstadoPago estado,
            CellStyle titleStyle,
            CellStyle subtitleStyle,
            CellStyle headerStyle,
            CellStyle textStyle,
            CellStyle moneyStyle,
            CellStyle intStyle,
            CellStyle totalStyle
    ) {

        int rowIdx = 0;

        // Title
        Row r0 = sheet.createRow(rowIdx++);
        Cell t = r0.createCell(0);
        t.setCellValue("Reporte de Pagos");
        t.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

        // Filtros
        Row r1 = sheet.createRow(rowIdx++);
        Cell f = r1.createCell(0);
        f.setCellValue("Filtros:");
        f.setCellStyle(subtitleStyle);

        rowIdx = addFiltroLine(sheet, rowIdx, "Desde", desde != null ? desde.format(DTF) : "Todos", textStyle);
        rowIdx = addFiltroLine(sheet, rowIdx, "Hasta", hasta != null ? hasta.format(DTF) : "Todos", textStyle);
        rowIdx = addFiltroLine(sheet, rowIdx, "Tipo Operación", tipoOperacion != null ? tipoOperacion.name() : "Todos", textStyle);
        rowIdx = addFiltroLine(sheet, rowIdx, "Método", metodo != null ? metodo.name() : "Todos", textStyle);
        rowIdx = addFiltroLine(sheet, rowIdx, "Estado", estado != null ? estado.name() : "Todos", textStyle);

        rowIdx++; // spacer

        // KPI
        BigDecimal totalMonto = pagos.stream()
                .map(Pago::getMonto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalPagos = pagos.size();

        BigDecimal ticketProm = totalPagos == 0
                ? BigDecimal.ZERO
                : totalMonto.divide(BigDecimal.valueOf(totalPagos), 2, RoundingMode.HALF_UP);

        Row kpiHeader = sheet.createRow(rowIdx++);
        createCell(kpiHeader, 0, "KPI", headerStyle);
        createCell(kpiHeader, 1, "Valor", headerStyle);

        Row k1 = sheet.createRow(rowIdx++);
        createCell(k1, 0, "Cantidad de pagos", textStyle);
        Cell cK1 = k1.createCell(1);
        cK1.setCellValue(totalPagos);
        cK1.setCellStyle(intStyle);

        Row k2 = sheet.createRow(rowIdx++);
        createCell(k2, 0, "Total recaudado", textStyle);
        Cell cK2 = k2.createCell(1);
        cK2.setCellValue(totalMonto.doubleValue());
        cK2.setCellStyle(moneyStyle);

        Row k3 = sheet.createRow(rowIdx++);
        createCell(k3, 0, "Ticket promedio", textStyle);
        Cell cK3 = k3.createCell(1);
        cK3.setCellValue(ticketProm.doubleValue());
        cK3.setCellStyle(moneyStyle);

        rowIdx += 2;

        createSectionTitle(sheet, rowIdx++, "Totales por Método de Pago", subtitleStyle, 0, 3);

        Row mHeader = sheet.createRow(rowIdx++);
        createCell(mHeader, 0, "Método", headerStyle);
        createCell(mHeader, 1, "Cantidad", headerStyle);
        createCell(mHeader, 2, "Total", headerStyle);

        Map<MetodoPago, Summary> porMetodo = new LinkedHashMap<>();
        for (Pago p : pagos) {
            MetodoPago mp = p.getMetodo();
            if (mp == null) continue;
            porMetodo.computeIfAbsent(mp, k -> new Summary()).add(p.getMonto());
        }

        for (var entry : porMetodo.entrySet()) {
            Row r = sheet.createRow(rowIdx++);
            createCell(r, 0, entry.getKey().name(), textStyle);

            Cell cc = r.createCell(1);
            cc.setCellValue(entry.getValue().count);
            cc.setCellStyle(intStyle);

            Cell ct = r.createCell(2);
            ct.setCellValue(entry.getValue().total.doubleValue());
            ct.setCellStyle(moneyStyle);
        }

        rowIdx += 2;
        createSectionTitle(sheet, rowIdx++, "Totales por Tipo de Operación", subtitleStyle, 0, 3);

        Row opHeader = sheet.createRow(rowIdx++);
        createCell(opHeader, 0, "Tipo Operación", headerStyle);
        createCell(opHeader, 1, "Cantidad", headerStyle);
        createCell(opHeader, 2, "Total", headerStyle);

        Map<TipoOperacionPago, Summary> porOperacion = new LinkedHashMap<>();
        for (Pago p : pagos) {
            TipoOperacionPago top = p.getTipoOperacion();
            if (top == null) continue;
            porOperacion.computeIfAbsent(top, k -> new Summary()).add(p.getMonto());
        }

        for (var entry : porOperacion.entrySet()) {
            Row r = sheet.createRow(rowIdx++);
            createCell(r, 0, entry.getKey().name(), textStyle);

            Cell cc = r.createCell(1);
            cc.setCellValue(entry.getValue().count);
            cc.setCellStyle(intStyle);

            Cell ct = r.createCell(2);
            ct.setCellValue(entry.getValue().total.doubleValue());
            ct.setCellStyle(moneyStyle);
        }

        rowIdx += 2;

        Row totalRow = sheet.createRow(rowIdx++);
        createCell(totalRow, 0, "TOTAL FINAL", totalStyle);
        Cell tf = totalRow.createCell(1);
        tf.setCellValue(totalMonto.doubleValue());
        tf.setCellStyle(totalStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                totalRow.getRowNum(), totalRow.getRowNum(), 1, 2
        ));

        for (int i = 0; i <= 5; i++) sheet.autoSizeColumn(i);
    }

    private int addFiltroLine(Sheet sheet, int rowIdx, String label, String value, CellStyle style) {
        Row r = sheet.createRow(rowIdx++);
        createCell(r, 0, label + ":", style);
        createCell(r, 1, value, style);
        return rowIdx;
    }

    private void createSectionTitle(Sheet sheet, int rowIdx, String title, CellStyle style, int colFrom, int colTo) {
        Row r = sheet.createRow(rowIdx);
        Cell c = r.createCell(colFrom);
        c.setCellValue(title);
        c.setCellStyle(style);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIdx, rowIdx, colFrom, colTo));
    }

    // ===================== Detalle Sheet =====================

    private void buildDetalleSheet(
            Sheet sheet,
            List<Pago> pagos,
            CellStyle titleStyle,
            CellStyle headerStyle,
            CellStyle textStyle,
            CellStyle dateStyle,
            CellStyle moneyStyle
    ) {

        int rowIdx = 0;

        Row titleRow = sheet.createRow(rowIdx++);
        Cell t = titleRow.createCell(0);
        t.setCellValue("Detalle de Pagos");
        t.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 12));

        Row header = sheet.createRow(rowIdx++);
        String[] cols = new String[]{
                "ID", "Fecha Pago", "Estado", "Método", "Moneda", "Monto",
                "Efectivo Recibido", "Cambio", "Ref. Transacción",
                "Tipo Operación", "Tipo Comprobante",
                "Cliente", "Factura"
        };

        for (int i = 0; i < cols.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(headerStyle);
        }

        BigDecimal totalMonto = BigDecimal.ZERO;

        for (Pago p : pagos) {
            Row r = sheet.createRow(rowIdx++);

            createCell(r, 0, p.getId() != null ? p.getId().toString() : "", textStyle);

            Cell fechaCell = r.createCell(1);
            fechaCell.setCellValue(p.getFechaPago() != null ? p.getFechaPago().format(DTF) : "");
            fechaCell.setCellStyle(dateStyle);

            createCell(r, 2, p.getEstado() != null ? p.getEstado().name() : "", textStyle);
            createCell(r, 3, p.getMetodo() != null ? p.getMetodo().name() : "", textStyle);
            createCell(r, 4, p.getMoneda() != null ? p.getMoneda() : "", textStyle);

            BigDecimal monto = p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO;
            Cell montoCell = r.createCell(5);
            montoCell.setCellValue(monto.doubleValue());
            montoCell.setCellStyle(moneyStyle);
            totalMonto = totalMonto.add(monto);

            BigDecimal ef = p.getEfectivoRecibido() != null ? p.getEfectivoRecibido() : BigDecimal.ZERO;
            Cell efCell = r.createCell(6);
            efCell.setCellValue(ef.doubleValue());
            efCell.setCellStyle(moneyStyle);

            BigDecimal cambio = p.getCambio() != null ? p.getCambio() : BigDecimal.ZERO;
            Cell cambioCell = r.createCell(7);
            cambioCell.setCellValue(cambio.doubleValue());
            cambioCell.setCellStyle(moneyStyle);

            createCell(r, 8, p.getReferenciaTransaccion() != null ? p.getReferenciaTransaccion() : "", textStyle);
            createCell(r, 9, p.getTipoOperacion() != null ? p.getTipoOperacion().name() : "", textStyle);
            createCell(r, 10, p.getTipoComprobante() != null ? p.getTipoComprobante().name() : "", textStyle);

            String nombreCliente = p.getCliente() != null
                    ? ((p.getCliente().getNombres() != null ? p.getCliente().getNombres() : "") + " " +
                    (p.getCliente().getApellidos() != null ? p.getCliente().getApellidos() : "")).trim()
                    : "";
            createCell(r, 11, nombreCliente, textStyle);

            String factura = (p.getVenta() != null && p.getVenta().getNumeroFactura() != null)
                    ? p.getVenta().getNumeroFactura()
                    : "";
            createCell(r, 12, factura, textStyle);
        }

        Row totalRow = sheet.createRow(rowIdx++);
        createCell(totalRow, 4, "TOTAL", createBoldStyle(sheet.getWorkbook()));
        Cell totalCell = totalRow.createCell(5);
        totalCell.setCellValue(totalMonto.doubleValue());
        totalCell.setCellStyle(createTotalMoneyStyle(sheet.getWorkbook()));

        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
    }

    // ===================== Styles helpers =====================

    private CellStyle createTitleStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);

        CellStyle st = wb.createCellStyle();
        st.setFont(font);
        return st;
    }

    private CellStyle createSubtitleStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);

        CellStyle st = wb.createCellStyle();
        st.setFont(font);
        return st;
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);

        CellStyle st = wb.createCellStyle();
        st.setFont(font);
        st.setBorderBottom(BorderStyle.THIN);
        st.setBorderTop(BorderStyle.THIN);
        st.setBorderLeft(BorderStyle.THIN);
        st.setBorderRight(BorderStyle.THIN);
        return st;
    }

    private CellStyle createTextStyle(Workbook wb) {
        CellStyle st = wb.createCellStyle();
        st.setWrapText(false);
        return st;
    }

    private CellStyle createDateStyle(Workbook wb) {
        return createTextStyle(wb);
    }

    private CellStyle createMoneyStyle(Workbook wb) {
        CellStyle st = wb.createCellStyle();
        DataFormat format = wb.createDataFormat();
        st.setDataFormat(format.getFormat("#,##0.00"));
        return st;
    }

    private CellStyle createIntStyle(Workbook wb) {
        CellStyle st = wb.createCellStyle();
        DataFormat format = wb.createDataFormat();
        st.setDataFormat(format.getFormat("0"));
        return st;
    }

    private CellStyle createTotalStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);

        CellStyle st = wb.createCellStyle();
        st.setFont(font);
        st.setBorderBottom(BorderStyle.THIN);
        st.setBorderTop(BorderStyle.THIN);
        st.setBorderLeft(BorderStyle.THIN);
        st.setBorderRight(BorderStyle.THIN);
        return st;
    }

    private CellStyle createBoldStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        CellStyle st = wb.createCellStyle();
        st.setFont(font);
        return st;
    }

    private CellStyle createTotalMoneyStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);

        CellStyle st = wb.createCellStyle();
        st.setFont(font);
        DataFormat format = wb.createDataFormat();
        st.setDataFormat(format.getFormat("#,##0.00"));
        return st;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private static class Summary {
        long count = 0;
        BigDecimal total = BigDecimal.ZERO;

        void add(BigDecimal monto) {
            count++;
            if (monto != null) total = total.add(monto);
        }
    }
}