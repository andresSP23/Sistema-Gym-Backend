package com.ansicode.SistemaAdministracionGym.gasto;

import com.ansicode.SistemaAdministracionGym.banco.BancoService;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.enums.EstadoGasto;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroService;
import com.ansicode.SistemaAdministracionGym.sucursal.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository repository;
    private final GastoMapper mapper;
    private final MovimientoDineroService movimientoDineroService;
    private final BancoService bancoService;
    private final SucursalRepository sucursalRepository;

    @Transactional
    public GastoResponse create(GastoRequest request, Authentication connectedUser) {
        // Valida sucursal
        if (!sucursalRepository.existsById(request.getSucursalId())) {
            throw new BussinessException(BusinessErrorCodes.SUCURSAL_NOT_FOUND);
        }

        Gasto gasto = mapper.toEntity(request);

        // Si se paga inmediatamente
        if (request.isPagarAhora()) {
            if (request.getMetodoPago() == null) {
                throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR); // Metodo requerido
            }
            procesarPago(gasto, request, connectedUser);
            gasto.setEstado(EstadoGasto.PAGADO);
            gasto.setFechaPago(LocalDate.now());
            gasto.setMetodoPago(request.getMetodoPago());
        } else {
            gasto.setEstado(EstadoGasto.PENDIENTE);
        }

        return mapper.toResponse(repository.save(gasto));
    }

    @Transactional
    public GastoResponse update(Long id, GastoRequest request) {
        Gasto gasto = repository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VALIDATION_ERROR));

        // Solo permitir editar si NO está pagado (simplificación por seguridad)
        if (gasto.getEstado() == EstadoGasto.PAGADO) {
            // Opcional: permitir editar campos no financieros
            // Por ahora restringimos
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }

        // Actualizar campos
        gasto.setNombre(request.getNombre());
        gasto.setDescripcion(request.getDescripcion());
        gasto.setCategoria(request.getCategoria());
        gasto.setFechaGasto(request.getFechaGasto());
        gasto.setSucursalId(request.getSucursalId());

        // Actualizar montos/cantidades (para SUMINISTROS o general)
        if (request.getCantidad() != null && request.getPrecioUnitario() != null) {
            gasto.setCantidad(request.getCantidad());
            gasto.setPrecioUnitario(request.getPrecioUnitario());
            // Recalcular monto
            java.math.BigDecimal total = request.getPrecioUnitario().multiply(request.getCantidad());
            gasto.setMonto(total);
        } else if (request.getMonto() != null) {
            gasto.setMonto(request.getMonto());
            gasto.setCantidad(null);
            gasto.setPrecioUnitario(null);
        }

        return mapper.toResponse(repository.save(gasto));
    }

    @Transactional
    public GastoResponse pagarGasto(Long id, PagarGastoRequest pagoRequest, Authentication connectedUser) {
        Gasto gasto = repository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VALIDATION_ERROR)); // Generic not found

        if (gasto.getEstado() == EstadoGasto.PAGADO) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR); // Ya pagado
        }

        // Creamos un wrapper temporal para reutilizar la lógica
        GastoRequest tempRequest = new GastoRequest();
        tempRequest.setMonto(gasto.getMonto());
        tempRequest.setNombre(gasto.getNombre());
        tempRequest.setSucursalId(gasto.getSucursalId());
        tempRequest.setMetodoPago(pagoRequest.getMetodoPago());
        tempRequest.setBancoId(pagoRequest.getBancoId());

        procesarPago(gasto, tempRequest, connectedUser);

        gasto.setEstado(EstadoGasto.PAGADO);
        gasto.setFechaPago(LocalDate.now());
        gasto.setMetodoPago(pagoRequest.getMetodoPago());

        return mapper.toResponse(repository.save(gasto));
    }

    private void procesarPago(Gasto gasto, GastoRequest request, Authentication connectedUser) {
        String descripcion = "Pago Gasto: " + request.getNombre();

        if (request.getMetodoPago() == MetodoPago.EFECTIVO) {
            MovimientoDineroCreateRequest mov = new MovimientoDineroCreateRequest();
            mov.setTipo(TipoMovimientoDinero.EGRESO);
            mov.setConcepto(ConceptoMovimientoDinero.GASTO);
            mov.setMetodo(MetodoPago.EFECTIVO);
            mov.setMonto(request.getMonto());
            mov.setDescripcion(descripcion);
            mov.setSucursalId(request.getSucursalId());
            // mov.setGastoId(gasto.getId()); // Si quisieras enlazarlo en BD

            movimientoDineroService.crearMovimiento(mov, connectedUser);

        } else if (request.getMetodoPago() == MetodoPago.TRANSFERENCIA) {
            if (request.getBancoId() == null) {
                throw new BussinessException(BusinessErrorCodes.PAGO_BANCO_ID_REQUIRED);
            }
            bancoService.registrarMovimiento(
                    request.getBancoId(),
                    TipoMovimientoBanco.EGRESO,
                    request.getMonto(),
                    descripcion,
                    "GASTO#G-" + gasto.getId() + " | " + request.getNombre(),
                    com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco.OTROS,
                    com.ansicode.SistemaAdministracionGym.enums.OrigenMovimientoBanco.GASTO);
        } else if (request.getMetodoPago() == MetodoPago.OTRO) {
            // Solo registrar movimiento informativo (Tracker)
            MovimientoDineroCreateRequest mov = new MovimientoDineroCreateRequest();
            mov.setTipo(TipoMovimientoDinero.EGRESO);
            mov.setConcepto(ConceptoMovimientoDinero.GASTO);
            mov.setMetodo(MetodoPago.OTRO);
            mov.setMonto(request.getMonto());
            mov.setDescripcion(descripcion + " (EXTERNO)");
            mov.setSucursalId(request.getSucursalId());

            movimientoDineroService.crearMovimiento(mov, connectedUser);
        }
    }

    public PageResponse<GastoResponse> findAll(Pageable pageable) {
        Page<Gasto> page = repository.findAll(pageable);
        List<GastoResponse> content = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<GastoResponse>builder()
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
        if (!repository.existsById(id))
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        repository.deleteById(id);
    }
}
