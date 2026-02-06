package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ansicode.SistemaAdministracionGym.banco.BancoService;
import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroService;
import org.springframework.security.core.Authentication;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipamientoService {

    private final EquipamientoRepository equipamientoRepository;
    private final EquipamientoMapper equipamientoMapper;
    private final BancoService bancoService;
    private final MovimientoDineroService movimientoDineroService;
    private final com.ansicode.SistemaAdministracionGym.sucursal.SucursalRepository sucursalRepository;

    @Transactional
    public EquipamientoResponse create(EquipamientoRequest request, Authentication connectedUser) {
        Equipamiento equipamiento = equipamientoMapper.toEquipamiento(request);
        Equipamiento saved = equipamientoRepository.save(equipamiento);

        // --- Financial Integration ---
        if (request.getCosto() != null && request.getCosto().compareTo(java.math.BigDecimal.ZERO) > 0
                && request.getMetodoPago() != null) {

            String descripcion = "Compra de Activo: " + request.getNombre();

            if (request.getMetodoPago() == MetodoPago.EFECTIVO) {
                if (request.getSucursalId() == null) {
                    throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR); // Or generic "Sucursal requerida
                                                                                       // para efectivo"
                }

                MovimientoDineroCreateRequest movRequest = new MovimientoDineroCreateRequest();
                movRequest.setTipo(TipoMovimientoDinero.EGRESO);
                movRequest.setConcepto(ConceptoMovimientoDinero.COMPRA_ACTIVO);
                movRequest.setMetodo(MetodoPago.EFECTIVO);
                movRequest.setMonto(request.getCosto()); // Use costo as amount
                movRequest.setDescripcion(descripcion);
                movRequest.setSucursalId(request.getSucursalId());

                // We don't link via ID but could store in description or generic linkage if we
                // added it to MovimientoDinero
                // MovimientoDinero currently supports productoId, not equipamientoId.
                // We can leave it unlinked or link via description.

                movimientoDineroService.crearMovimiento(movRequest, connectedUser);

            } else if (request.getMetodoPago() == MetodoPago.TRANSFERENCIA) {
                if (request.getBancoId() == null) {
                    throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR); // Or generic "Banco requerido"
                }
                bancoService.registrarMovimiento(
                        request.getBancoId(),
                        TipoMovimientoBanco.EGRESO,
                        request.getCosto(),
                        descripcion,
                        "EQUIPAMIENTO#E-" + saved.getId() + " | " + request.getNombre(),
                        com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco.COMPRA_EQUIPAMIENTO,
                        com.ansicode.SistemaAdministracionGym.enums.OrigenMovimientoBanco.MANUAL);
            } else if (request.getMetodoPago() == MetodoPago.OTRO) {
                Long sucursalId = request.getSucursalId();
                if (sucursalId == null) {
                    // Fallback: usar la primera sucursal activa si no se envió ninguna
                    // Esto permite registrar gastos "externos" sin obligar al usuario a elegir
                    // sucursal en el frontend
                    sucursalId = sucursalRepository.findFirstByIsVisibleTrue()
                            .map(com.ansicode.SistemaAdministracionGym.sucursal.Sucursal::getId)
                            .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SUCURSAL_NOT_FOUND));
                }

                MovimientoDineroCreateRequest movRequest = new MovimientoDineroCreateRequest();
                movRequest.setTipo(TipoMovimientoDinero.EGRESO);
                movRequest.setConcepto(ConceptoMovimientoDinero.COMPRA_ACTIVO);
                movRequest.setMetodo(MetodoPago.OTRO);
                movRequest.setMonto(request.getCosto());
                movRequest.setDescripcion(descripcion + " (EXTERNO)");
                movRequest.setSucursalId(sucursalId);

                movimientoDineroService.crearMovimiento(movRequest, connectedUser);
            }
        }

        return equipamientoMapper.toEquipamientoResponse(saved);
    }

    @Transactional
    public EquipamientoResponse update(Long id, EquipamientoRequest request) {
        Equipamiento equipamiento = equipamientoRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.EQUIPAMIENTO_NOT_FOUND)); // Assuming you
                                                                                                       // have this code
                                                                                                       // or use generic

        // Manual update or mapper update
        equipamientoMapper.updateEquipamientoFromRequest(equipamiento, request);

        return equipamientoMapper.toEquipamientoResponse(equipamientoRepository.save(equipamiento));
    }

    public EquipamientoResponse findById(Long id) {
        return equipamientoRepository.findById(id)
                .map(equipamientoMapper::toEquipamientoResponse)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.EQUIPAMIENTO_NOT_FOUND));
    }

    public PageResponse<EquipamientoResponse> findAll(Pageable pageable) {
        Page<Equipamiento> page = equipamientoRepository.findAll(pageable);
        List<EquipamientoResponse> content = page.getContent().stream()
                .map(equipamientoMapper::toEquipamientoResponse)
                .toList();

        return PageResponse.<EquipamientoResponse>builder()
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
        if (!equipamientoRepository.existsById(id)) {
            throw new BussinessException(BusinessErrorCodes.EQUIPAMIENTO_NOT_FOUND);
        }
        equipamientoRepository.deleteById(id);
    }
}
