package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import com.ansicode.SistemaAdministracionGym.banco.Banco;
import com.ansicode.SistemaAdministracionGym.banco.BancoRepository;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.enums.OrigenMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servicio para gestión de movimientos bancarios.
 */
@Service
@RequiredArgsConstructor
public class MovimientoBancoService {

    private final MovimientoBancoRepository movimientoBancoRepository;
    private final BancoRepository bancoRepository;

    /**
     * Lista movimientos bancarios con filtros y ordenamiento DESC.
     */
    @Transactional(readOnly = true)
    public PageResponse<MovimientoBancoResponse> listarMovimientos(
            Long bancoId,
            TipoMovimientoBanco tipo,
            ConceptoMovimientoBanco concepto,
            LocalDateTime desde,
            LocalDateTime hasta,
            String textoLibre,
            Pageable pageable) {

        // Validar banco existe
        if (bancoId != null && !bancoRepository.existsById(bancoId)) {
            throw new BussinessException(BusinessErrorCodes.BANCO_NOT_FOUND);
        }

        // Forzar ordenamiento DESC por fecha e id
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "fecha").and(Sort.by(Sort.Direction.DESC, "id")));

        Specification<MovimientoBanco> spec = MovimientoBancoSpecifications.conFiltros(
                bancoId, tipo, concepto, desde, hasta, textoLibre);

        Page<MovimientoBanco> page = movimientoBancoRepository.findAll(spec, sortedPageable);

        return PageResponse.<MovimientoBancoResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    /**
     * Crea un movimiento bancario manual.
     */
    @Transactional
    public MovimientoBancoResponse crearMovimientoManual(Long bancoId, MovimientoBancoRequest request) {
        Banco banco = bancoRepository.findById(bancoId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.BANCO_NOT_FOUND));

        // Actualizar saldo del banco
        if (request.getTipo() == TipoMovimientoBanco.INGRESO) {
            banco.setSaldo(banco.getSaldo().add(request.getMonto()));
        } else {
            banco.setSaldo(banco.getSaldo().subtract(request.getMonto()));
        }
        bancoRepository.save(banco);

        // Crear movimiento
        MovimientoBanco movimiento = MovimientoBanco.builder()
                .banco(banco)
                .tipo(request.getTipo())
                .concepto(request.getConcepto())
                .origen(OrigenMovimientoBanco.MANUAL)
                .monto(request.getMonto())
                .fecha(request.getFecha() != null ? request.getFecha() : LocalDateTime.now())
                .descripcion(request.getDescripcion())
                .referencia(request.getReferencia())
                .build();

        MovimientoBanco saved = movimientoBancoRepository.save(movimiento);
        return toResponse(saved);
    }

    /**
     * Convierte entidad a response DTO.
     */
    private MovimientoBancoResponse toResponse(MovimientoBanco m) {
        return MovimientoBancoResponse.builder()
                .id(m.getId())
                .tipo(m.getTipo())
                .concepto(m.getConcepto())
                .origen(m.getOrigen())
                .monto(m.getMonto())
                .fecha(m.getFecha())
                .descripcion(m.getDescripcion())
                .referencia(m.getReferencia())
                .bancoId(m.getBanco() != null ? m.getBanco().getId() : null)
                .bancoNombre(m.getBanco() != null ? m.getBanco().getNombre() : null)
                .build();
    }
}
