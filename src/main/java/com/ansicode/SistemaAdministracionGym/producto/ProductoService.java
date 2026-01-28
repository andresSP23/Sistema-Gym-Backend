package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProducto;
import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProductoRepository;
import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteResponse;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDinero;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroRepository;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroService;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioService;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaService;
import com.ansicode.SistemaAdministracionGym.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioService movimientoService;
    private final ProductoMapper productoMapper;
    private final CategoriaProductoRepository categoriaProductoRepository;

    private final MovimientoDineroService movimientoDineroService;

    // (los dejo porque tú los tenías, pero aquí no se usan)
    private final SesionCajaService sesionCajaService;
    private final MovimientoDineroRepository movimientoDineroRepository;

    @Transactional
    public ProductoResponse create(ProductoRequest request) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getCategoriaProductoId() == null) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_CATEGORIA_REQUIRED);
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_NOMBRE_REQUIRED);
        }
        if (request.getPrecioCompra() == null || request.getPrecioCompra().compareTo(BigDecimal.ZERO) < 0) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_PRECIO_COMPRA_INVALIDO);
        }
        if (request.getPrecioVenta() == null || request.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_PRECIO_VENTA_INVALIDO);
        }

        CategoriaProducto categoria = categoriaProductoRepository
                .findById(request.getCategoriaProductoId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PRODUCTO_CATEGORIA_NOT_FOUND));

        Producto producto = productoMapper.toEntity(request, categoria);

        // stock por defecto
        producto.setStock(0);

        Producto saved = productoRepository.save(producto);

        return productoMapper.toProductoResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductoResponse> findAll(Pageable pageable) {

        Page<Producto> page = productoRepository.findAll(pageable);

        return PageResponse.<ProductoResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(productoMapper::toProductoResponse)
                                .toList()
                )
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PRODUCTO_NOT_FOUND));

        return productoMapper.toProductoResponse(producto);
    }

    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getCategoriaProductoId() == null) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_CATEGORIA_REQUIRED);
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_NOMBRE_REQUIRED);
        }
        if (request.getPrecioCompra() == null || request.getPrecioCompra().compareTo(BigDecimal.ZERO) < 0) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_PRECIO_COMPRA_INVALIDO);
        }
        if (request.getPrecioVenta() == null || request.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_PRECIO_VENTA_INVALIDO);
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PRODUCTO_NOT_FOUND));

        CategoriaProducto categoria = categoriaProductoRepository
                .findById(request.getCategoriaProductoId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PRODUCTO_CATEGORIA_NOT_FOUND));

        producto.setNombre(request.getNombre());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setCategoriaProducto(categoria);

        // (si tu JPA está bien, no hace falta save; pero no estorba)
        Producto saved = productoRepository.save(producto);

        return productoMapper.toProductoResponse(saved);
    }

    @Transactional
    public void delete(Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PRODUCTO_NOT_FOUND));

        productoRepository.delete(producto);
    }

    @Transactional
    public void agregarStock(Long productoId, AgregarStockRequest request, Authentication connectedUser) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_STOCK_CANTIDAD_INVALIDA);
        }

        Producto producto = productoRepository.findByIdForUpdate(productoId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PRODUCTO_NOT_FOUND));

        if (producto.getPrecioCompra() == null || producto.getPrecioCompra().compareTo(BigDecimal.ZERO) < 0) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_SIN_PRECIO_COMPRA);
        }

        BigDecimal montoTotal = money(producto.getPrecioCompra()
                .multiply(BigDecimal.valueOf(request.getCantidad())));

        // 1) Inventario: entrada
        movimientoService.registrarEntrada(producto, request.getCantidad(), request.getObservacion());

        // asegurar persistencia stock si tu movimientoService no guarda producto
        productoRepository.save(producto);

        // 2) Caja/dinero: egreso (opcional)
        if (Boolean.TRUE.equals(request.getRegistrarEgreso())) {

            if (request.getSucursalId() == null) {
                throw new BussinessException(BusinessErrorCodes.PRODUCTO_SUCURSAL_REQUIRED_PARA_EGRESO);
            }
            if (request.getMetodoPago() == null) {
                throw new BussinessException(BusinessErrorCodes.PRODUCTO_METODO_PAGO_REQUIRED_PARA_EGRESO);
            }
            if (connectedUser == null || connectedUser.getPrincipal() == null) {
                throw new BussinessException(BusinessErrorCodes.BAD_CREDENTIALS);
            }

            MovimientoDineroCreateRequest mdRequest = new MovimientoDineroCreateRequest();
            mdRequest.setSucursalId(request.getSucursalId());
            mdRequest.setTipo(TipoMovimientoDinero.EGRESO);
            mdRequest.setConcepto(ConceptoMovimientoDinero.COMPRA_STOCK);
            mdRequest.setMetodo(request.getMetodoPago());
            mdRequest.setMoneda(normalizeMoneda(request.getMoneda()));
            mdRequest.setMonto(montoTotal);
            mdRequest.setDescripcion(request.getObservacion());
            mdRequest.setProductoId(productoId);

            movimientoDineroService.crearMovimiento(mdRequest, connectedUser);
        }
    }

    @Transactional
    public void ajustarStock(Long productoId, AjustarStockRequest request) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getStockReal() == null || request.getStockReal() < 0) {
            throw new BussinessException(BusinessErrorCodes.PRODUCTO_AJUSTE_STOCK_REAL_INVALIDO);
        }

        Producto producto = productoRepository.findByIdForUpdate(productoId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PRODUCTO_NOT_FOUND));

        // Inventario: ajuste (conteo físico)
        movimientoService.registrarAjuste(producto, request.getStockReal(), request.getObservacion());

        // asegurar persistencia stock
        productoRepository.save(producto);

        // Regla: AJUSTE no debería mover caja automáticamente
    }

    private String normalizeMoneda(String moneda) {
        return (moneda == null || moneda.isBlank()) ? "USD" : moneda.trim().toUpperCase();
    }

    private BigDecimal money(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }
}