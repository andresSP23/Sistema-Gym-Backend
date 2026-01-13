package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProducto;
import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProductoRepository;
import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteResponse;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioService;
import com.ansicode.SistemaAdministracionGym.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductoService {


    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final ProductoMapper productoMapper;
    private final MovimientoInventarioService movimientoService;

    @Transactional
    public ProductoResponse create(ProductoRequest request) {

        CategoriaProducto categoria = categoriaProductoRepository
                .findById(request.getCategoriaProductoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoría no encontrada")
                );

        Producto producto = productoMapper.toEntity(request, categoria);
        producto.setActivo(true);
        producto.setStock(0);

        // GUARDAR PRIMERO EL PRODUCTO
        producto = productoRepository.save(producto);

        //  REGISTRAR MOVIMIENTO DESPUÉS
        if (request.getStockInicial() != null && request.getStockInicial() > 0) {

            movimientoService.registrarEntrada(
                    producto,
                    request.getStockInicial()
            );

            producto.setStock(request.getStockInicial());
        }

        return productoMapper.toProductoResponse(producto);
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
    public void agregarStock(
            Long productoId,
            Integer cantidad

    ) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

        movimientoService.registrarEntrada(producto, cantidad);
    }

    @Transactional
    public void ajustarStock(
            Long productoId,
            Integer stockReal

    ) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

        movimientoService.registrarAjuste(producto, stockReal);
    }
}