package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoInventario;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import com.ansicode.SistemaAdministracionGym.user.User;
import com.ansicode.SistemaAdministracionGym.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;
    private final ProductoRepository productoRepository; // (lo mantengo aunque aquí no lo uses)
    private final MovimientoInventarioMapper mapper;

    @Transactional
    public void registrarEntrada(Producto producto, Integer cantidad) {
        registrarEntrada(producto, cantidad, null);
    }

    @Transactional
    public void registrarEntrada(Producto producto, Integer cantidad, String observacion) {
        registrar(producto, cantidad, TipoMovimientoInventario.ENTRADA, observacion);
    }

    @Transactional
    public void registrarSalida(Producto producto, Integer cantidad) {
        registrarSalida(producto, cantidad, null);
    }

    @Transactional
    public void registrarSalida(Producto producto, Integer cantidad, String observacion) {
        registrar(producto, cantidad, TipoMovimientoInventario.SALIDA, observacion);
    }

    @Transactional
    public void registrarAjuste(Producto producto, Integer stockReal) {
        registrarAjuste(producto, stockReal, null);
    }

    @Transactional
    public void registrarAjuste(Producto producto, Integer stockReal, String observacion) {
        registrar(producto, stockReal, TipoMovimientoInventario.AJUSTE, observacion);
    }

    private void registrar(Producto producto, Integer valor, TipoMovimientoInventario tipo, String observacion) {

        if (producto == null) {
            throw new BussinessException(BusinessErrorCodes.INVENTARIO_PRODUCTO_REQUIRED);
        }
        if (tipo == null) {
            throw new BussinessException(BusinessErrorCodes.INVENTARIO_TIPO_REQUIRED);
        }
        if (valor == null) {
            throw new BussinessException(BusinessErrorCodes.INVENTARIO_VALOR_REQUIRED);
        }

        int stockAnterior = producto.getStock() == null ? 0 : producto.getStock();

        // Validaciones
        if (tipo != TipoMovimientoInventario.AJUSTE && valor <= 0) {
            throw new BussinessException(BusinessErrorCodes.INVENTARIO_CANTIDAD_INVALIDA);
        }
        if (tipo == TipoMovimientoInventario.AJUSTE && valor < 0) {
            throw new BussinessException(BusinessErrorCodes.INVENTARIO_STOCK_REAL_INVALIDO);
        }
        if (tipo == TipoMovimientoInventario.SALIDA && stockAnterior < valor) {
            throw new BussinessException(BusinessErrorCodes.INVENTARIO_STOCK_INSUFICIENTE);
        }

        int stockNuevo = switch (tipo) {
            case ENTRADA -> stockAnterior + valor;
            case SALIDA -> stockAnterior - valor;
            case AJUSTE -> valor; // valor = stockReal
        };

        // ENTRADA/SALIDA: cantidad = unidades
        // AJUSTE: cantidad = delta (diferencia real)
        int cantidadRegistrada = (tipo == TipoMovimientoInventario.AJUSTE)
                ? (stockNuevo - stockAnterior)
                : valor;

        MovimientoInventario m = MovimientoInventario.builder()
                .producto(producto)
                .tipoMovimiento(tipo)
                .cantidad(cantidadRegistrada)
                .stockAnterior(stockAnterior)
                .stockActual(stockNuevo)
                .isVisible(true) // Explícito por seguridad con SuperBuilder
                .observacion((observacion != null && !observacion.isBlank())
                        ? (observacion.length() > 250 ? observacion.substring(0, 250) : observacion)
                        : null)
                .build();

        producto.setStock(stockNuevo);

        repository.saveAndFlush(m);
    }

    @Transactional(readOnly = true)
    public PageResponse<MovimientoInventarioResponse> listar(
            Pageable pageable,
            Long productoId,
            String tipoMovimiento,
            LocalDateTime desde,
            LocalDateTime hasta,
            Long createdBy,
            Integer cantidadMin,
            Integer cantidadMax,
            Integer stockActualMin,
            Integer stockActualMax,
            String q) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BussinessException(BusinessErrorCodes.INVENTARIO_RANGO_FECHAS_INVALIDO);
        }

        Specification<MovimientoInventario> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and(MovimientoInventarioSpecifications.productoId(productoId));
        spec = spec.and(MovimientoInventarioSpecifications.tipoMovimiento(tipoMovimiento));
        spec = spec.and(MovimientoInventarioSpecifications.fechaDesde(desde));
        spec = spec.and(MovimientoInventarioSpecifications.fechaHasta(hasta));
        spec = spec.and(MovimientoInventarioSpecifications.createdBy(createdBy));
        spec = spec.and(MovimientoInventarioSpecifications.cantidadMin(cantidadMin));
        spec = spec.and(MovimientoInventarioSpecifications.cantidadMax(cantidadMax));
        spec = spec.and(MovimientoInventarioSpecifications.stockActualMin(stockActualMin));
        spec = spec.and(MovimientoInventarioSpecifications.stockActualMax(stockActualMax));
        spec = spec.and(MovimientoInventarioSpecifications.search(q));

        Page<MovimientoInventario> page = repository.findAll(spec, pageable);

        List<MovimientoInventarioResponse> content = page.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<MovimientoInventarioResponse>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}