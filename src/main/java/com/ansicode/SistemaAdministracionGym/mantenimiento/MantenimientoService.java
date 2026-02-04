package com.ansicode.SistemaAdministracionGym.mantenimiento;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.equipamiento.Equipamiento;
import com.ansicode.SistemaAdministracionGym.equipamiento.EquipamientoRepository;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.enums.TipoMantenimiento;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MantenimientoService {

    private final MantenimientoRepository repository;
    private final EquipamientoRepository equipamientoRepository;
    private final MantenimientoMapper mapper;

    private final com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroService movimientoDineroService;
    private final com.ansicode.SistemaAdministracionGym.banco.BancoService bancoService;

    @Transactional
    public MantenimientoResponse create(MantenimientoRequest request,
            org.springframework.security.core.Authentication connectedUser) {
        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }

        Equipamiento equip = equipamientoRepository.findById(request.getEquipamientoId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.EQUIPAMIENTO_NOT_FOUND));

        Mantenimiento mantenimiento = mapper.toEntity(request, equip);

        // Calcular próximo mantenimiento si es preventivo y el equipo tiene frecuencia
        if (request.getTipo() == TipoMantenimiento.PREVENTIVO &&
                equip.getFrecuenciaMantenimientoDias() != null &&
                equip.getFrecuenciaMantenimientoDias() > 0) {

            LocalDate nextDate = request.getFechaRealizacion().toLocalDate()
                    .plusDays(equip.getFrecuenciaMantenimientoDias());
            mantenimiento.setProximoMantenimientoSugerido(nextDate);

            // Actualizar el equipo también
            equip.setProximoMantenimiento(nextDate);
            equipamientoRepository.save(equip);
        }

        Mantenimiento saved = repository.save(mantenimiento);

        // Registrar Egreso de Dinero (Caja o Banco)
        if (request.getCosto() != null && request.getCosto().compareTo(java.math.BigDecimal.ZERO) > 0) {
            String descripcion = "Mantenimiento " + request.getTipo() + " - " + equip.getNombre();

            if (request.getMetodoPago() == com.ansicode.SistemaAdministracionGym.enums.MetodoPago.TRANSFERENCIA ||
                    request.getMetodoPago() == com.ansicode.SistemaAdministracionGym.enums.MetodoPago.TARJETA) {

                // Banco
                if (request.getBancoId() == null) {
                    throw new BussinessException(BusinessErrorCodes.PAGO_BANCO_ID_REQUIRED);
                }

                bancoService.registrarMovimiento(
                        request.getBancoId(),
                        com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco.EGRESO,
                        request.getCosto(),
                        request.getDescripcion() != null ? request.getDescripcion() : descripcion,
                        "MANTENIMIENTO#M-" + saved.getId() + " | EQUIPAMIENTO#E-" + equip.getId(),
                        com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco.MANTENIMIENTO,
                        com.ansicode.SistemaAdministracionGym.enums.OrigenMovimientoBanco.GASTO);

            } else {
                // Caja (Efectivo / Default)
                // Se requiere sucursalId, pero Mantenimiento no lo tiene directamente en el
                // request.
                // Asumimos la sucursal del equipo si la tuviera, o del usuario conectado.
                // Por simplicidad y consistencia con otros módulos, asumiremos que si es
                // efectivo,
                // el usuario debe proveer sucursalId o lo tomamos del contexto si existiera.
                // Como MantenimientoRequest no tiene sucursalId, usaremos una lógica
                // simplificada
                // o lanzaremos error si es crítico.
                // *MEJORA*: Agregar sucursalId a MantenimientoRequest O tomarla del
                // Equipamiento -> Sucursal

                Long sucursalId = equip.getSucursal() != null ? equip.getSucursal().getId() : null;
                if (sucursalId == null) {
                    // Fallback o error. Por ahora validamos
                    throw new BussinessException(BusinessErrorCodes.PRODUCTO_SUCURSAL_REQUIRED_PARA_EGRESO);
                }

                com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest mdRequest = new com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest();
                mdRequest.setSucursalId(sucursalId);
                mdRequest.setTipo(com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero.EGRESO);
                mdRequest.setConcepto(
                        com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero.COMPRA_ACTIVO);
                mdRequest.setMetodo(request.getMetodoPago() != null ? request.getMetodoPago()
                        : com.ansicode.SistemaAdministracionGym.enums.MetodoPago.EFECTIVO);
                mdRequest.setMoneda(normalizeMoneda(request.getMoneda()));
                mdRequest.setMonto(request.getCosto());
                mdRequest.setDescripcion(request.getDescripcion() != null ? request.getDescripcion() : descripcion);

                movimientoDineroService.crearMovimiento(mdRequest, connectedUser);
            }
        }

        return mapper.toResponse(saved);
    }

    private String normalizeMoneda(String moneda) {
        return (moneda == null || moneda.isBlank()) ? "USD" : moneda.trim().toUpperCase();
    }

    public PageResponse<MantenimientoResponse> findAll(Pageable pageable) {
        Page<Mantenimiento> page = repository.findAll(pageable);
        List<MantenimientoResponse> content = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<MantenimientoResponse>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public List<MantenimientoResponse> findByEquipamiento(Long equipamientoId) {
        return repository.findByEquipamientoId(equipamientoId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            // throw not found... reusing generic or specific
            throw new RuntimeException("Mantenimiento no encontrado");
        }
        repository.deleteById(id);
    }
}
