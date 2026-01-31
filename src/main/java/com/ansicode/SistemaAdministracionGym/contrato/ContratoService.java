package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
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
    private final String uploadDir = "uploads/contratos/";

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

    @Transactional
    public void delete(Long id) {
        if (!contratoRepository.existsById(id)) {
            throw new BussinessException(BusinessErrorCodes.CONTRATO_NOT_FOUND);
        }
        contratoRepository.deleteById(id);
    }

    public byte[] generarContratoPdf(Long contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CONTRATO_NOT_FOUND));
        Cliente cliente = contrato.getCliente();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            // Título
            doc.add(new Paragraph("CONTRATO DE ADHESIÓN GYM FLOW")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));

            // Cuerpo
            String texto = "Por el presente documento, el gimnasio GYM FLOW celebra un contrato de prestación de servicios deportivos con el socio:\n\n"
                    +
                    "Nombre: " + cliente.getNombres() + " " + cliente.getApellidos() + "\n" +
                    "Identificación: " + cliente.getCedula() + "\n" +
                    "Teléfono: " + cliente.getTelefono() + "\n\n" +
                    "TÉRMINOS Y CONDICIONES:\n" +
                    "1. El socio se compromete a respetar las normas del establecimiento.\n" +
                    "2. El gimnasio no se hace responsable por objetos perdidos.\n" +
                    "3. La suscripción es personal e intransferible.\n" +
                    "4. En caso de enfermedad, se podrá congelar la membresía presentando certificado médico.\n\n" +
                    "Fecha de emisión: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            doc.add(new Paragraph(texto));
            doc.add(new Paragraph("\n\n\n\n\n"));

            // Firmas
            doc.add(new Paragraph("__________________________             __________________________")
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Firma del Socio                                   Firma del Administrador")
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new BussinessException(BusinessErrorCodes.COMPROBANTE_PDF_GENERATION_FAILED); // Reutilizamos error de
                                                                                                // PDF
        }
    }

    @Transactional
    public ContratoResponse subirContratoFirmado(Long contratoId, MultipartFile file) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CONTRATO_NOT_FOUND));

        try {
            if (file.isEmpty()) {
                throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
            }

            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Guardar archivo
            Files.copy(file.getInputStream(), filePath);

            // Actualizar entidad
            contrato.setArchivoUrl(filePath.toString());
            contrato.setEstadoContrato(com.ansicode.SistemaAdministracionGym.enums.EstadoContrato.ACTIVO); // Asumimos
                                                                                                           // que al
                                                                                                           // subir ya
                                                                                                           // es activo

            return contratoMapper.toContratoResponse(contratoRepository.save(contrato));

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }
}
