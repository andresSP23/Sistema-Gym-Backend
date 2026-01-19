package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProducto;
import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProductoRepository;
import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteResponse;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDinero;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroCreateRequest;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroRepository;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroService;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioService;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaService;
import com.ansicode.SistemaAdministracionGym.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductoService {


    private final ProductoRepository productoRepository;
    private final MovimientoInventarioService movimientoService;
    private final ProductoMapper productoMapper;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final MovimientoDineroService  movimientoDineroService;
    // Caja / dinero:
    private final SesionCajaService sesionCajaService; // obtiene sesión abierta por sucursal
    private final MovimientoDineroRepository movimientoDineroRepository;

    @Transactional
    public ProductoResponse create(ProductoRequest request) {

        CategoriaProducto categoria = categoriaProductoRepository
                .findById(request.getCategoriaProductoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoría no encontrada")
                );
    // Crear producto desde request (sin stock)

    Producto producto = productoMapper.toEntity(request, categoria);

        //stock por defecto
        producto.setStock(0);

        // Guardar y retornar
        Producto saved = productoRepository.save(producto);

        return productoMapper.toProductoResponse(saved);
    }


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

    public ProductoResponse findById(Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

        return productoMapper.toProductoResponse(producto);
    }

    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

        CategoriaProducto categoria = categoriaProductoRepository
                .findById(request.getCategoriaProductoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoría no encontrada")
                );

        producto.setNombre(request.getNombre());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setCategoriaProducto(categoria);

        return productoMapper.toProductoResponse(producto);
    }

    public void delete(Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

        productoRepository.delete(producto);
    }



    @Transactional
    public void agregarStock(Long productoId, AgregarStockRequest request , Authentication connectedUser) {

        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        // 1) Traer producto con lock
        Producto producto = productoRepository.findByIdForUpdate(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // 2) Inventario: entrada
        movimientoService.registrarEntrada(producto, request.getCantidad(), request.getObservacion());

        // 3) Dinero  egreso por compra de stock
        if (Boolean.TRUE.equals(request.getRegistrarEgreso())) {

            if (request.getSucursalId() == null) {
                throw new IllegalArgumentException("sucursalId es obligatorio cuando registrarEgreso = true");
            }
            if (request.getCostoTotal() == null || request.getCostoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("costoTotal es requerido y debe ser > 0 cuando registrarEgreso = true");
            }
            if (request.getMetodoPago() == null) {
                throw new IllegalArgumentException("metodoPago es requerido cuando registrarEgreso = true");
            }

            // Crear movimiento global (EGRESO)
            MovimientoDineroCreateRequest mdRequest = new MovimientoDineroCreateRequest();
            mdRequest.setSucursalId(request.getSucursalId());
            mdRequest.setTipo(TipoMovimientoDinero.EGRESO);
            mdRequest.setConcepto(ConceptoMovimientoDinero.COMPRA_STOCK);
            mdRequest.setMetodo(request.getMetodoPago());
            mdRequest.setMoneda(request.getMoneda() == null ? "USD" : request.getMoneda());
            mdRequest.setMonto(request.getCostoTotal());
            mdRequest.setDescripcion(request.getObservacion());
            mdRequest.setProductoId(productoId);

            movimientoDineroService.crearMovimiento(mdRequest , connectedUser);
        }
    }

    @Transactional
    public void ajustarStock(Long productoId, AjustarStockRequest request) {

        if (request.getStockReal() == null || request.getStockReal() < 0) {
            throw new IllegalArgumentException("El stock real no puede ser negativo");
        }

        Producto producto = productoRepository.findByIdForUpdate(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // Inventario: ajuste (conteo físico)
        movimientoService.registrarAjuste(producto, request.getStockReal(), request.getObservacion());

        // Regla: AJUSTE no debería mover caja automáticamente

    }
}