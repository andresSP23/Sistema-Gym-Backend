package com.ansicode.SistemaAdministracionGym.movimientodinero;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import com.ansicode.SistemaAdministracionGym.pago.PagoRepository;
import com.ansicode.SistemaAdministracionGym.servicio.Servicios;
import com.ansicode.SistemaAdministracionGym.servicio.ServiciosRepository;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaService;
import com.ansicode.SistemaAdministracionGym.user.User;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import com.ansicode.SistemaAdministracionGym.venta.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MovimientoDineroService {

    private final MovimientoDineroRepository repository;
    private final MovimientoDineroMapper mapper;
    private final SesionCajaService sesionCajaService;
    private final PagoRepository pagoRepository;
    private final ServiciosRepository serviciosRepository;
    private final VentaRepository ventaRepository;

    @Transactional
    public MovimientoDineroResponse crearMovimiento(MovimientoDineroCreateRequest request, Authentication connectedUser) {

        // ✅ Seguridad extra (evitar NPE)
        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }

        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_MONTO_INVALIDO);
        }
        if (request.getTipo() == null) {
            throw new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_TIPO_REQUIRED);
        }
        if (request.getConcepto() == null) {
            throw new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_CONCEPTO_REQUIRED);
        }
        if (request.getMetodo() == null) {
            throw new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_METODO_REQUIRED);
        }
        if (request.getSucursalId() == null) {
            throw new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_SUCURSAL_REQUIRED);
        }
        if (connectedUser == null || connectedUser.getPrincipal() == null) {
            throw new BussinessException(BusinessErrorCodes.BAD_CREDENTIALS);
        }

        // 1) Sesión abierta (si no existe, que tu SesionCajaService lance BussinessException)
        SesionCaja sesion = sesionCajaService.obtenerSesionAbiertaPorSucursal(request.getSucursalId());

        // 2) Crear entity
        MovimientoDinero m = new MovimientoDinero();
        m.setSesionCaja(sesion);
        m.setTipo(request.getTipo());
        m.setConcepto(request.getConcepto());
        m.setMetodo(request.getMetodo());
        m.setMoneda(normalizeMoneda(request.getMoneda()));
        m.setMonto(request.getMonto());
        m.setDescripcion(request.getDescripcion());

        // ✅ Trazabilidad
        if (request.getVentaId() != null) {
            Venta venta = ventaRepository.findById(request.getVentaId())
                    .orElseThrow(() -> new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_VENTA_NOT_FOUND));
            m.setVenta(venta);
        }

        if (request.getPagoId() != null) {
            Pago pago = pagoRepository.findById(request.getPagoId())
                    .orElseThrow(() -> new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_PAGO_NOT_FOUND));
            m.setPago(pago);
        }

        if (request.getServicioId() != null) {
            Servicios servicio = serviciosRepository.findById(request.getServicioId())
                    .orElseThrow(() -> new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_SERVICIO_NOT_FOUND));
            m.setServicio(servicio);
        }

        m.setProductoId(request.getProductoId());

        User user = (User) connectedUser.getPrincipal();
        m.setUsuarioId(user.getId());

        // 3) Guardar
        MovimientoDinero saved = repository.save(m);

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<MovimientoDineroResponse> listarTodos(
            String tipo,
            String concepto,
            String metodo,
            String moneda,
            Long usuarioId,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable
    ) {
        // ✅ Validación rango fechas (evita filtros raros)
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BussinessException(BusinessErrorCodes.MOVIMIENTO_DINERO_RANGO_FECHAS_INVALIDO);
        }

        Specification<MovimientoDinero> spec =
                MovimientoDineroSpecifications.tipo(tipo)
                        .and(MovimientoDineroSpecifications.concepto(concepto))
                        .and(MovimientoDineroSpecifications.metodo(metodo))
                        .and(MovimientoDineroSpecifications.moneda(moneda))
                        .and(MovimientoDineroSpecifications.usuarioId(usuarioId))
                        .and(MovimientoDineroSpecifications.fechaDesde(desde))
                        .and(MovimientoDineroSpecifications.fechaHasta(hasta));

        Page<MovimientoDinero> page = repository.findAll(spec, pageable);

        List<MovimientoDineroResponse> content = page.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<MovimientoDineroResponse>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private String normalizeMoneda(String moneda) {
        return (moneda == null || moneda.isBlank()) ? "USD" : moneda.trim().toUpperCase();
    }
}