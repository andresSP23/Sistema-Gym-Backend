package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoInventario;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import com.ansicode.SistemaAdministracionGym.user.User;
import com.ansicode.SistemaAdministracionGym.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioMapper mapper;

    @Transactional
    public void registrarEntrada(
            Producto producto,
            Integer cantidad
    ) {
        registrar(producto, cantidad, TipoMovimientoInventario.ENTRADA);
    }

    @Transactional
    public void registrarSalida(
            Producto producto,
            Integer cantidad,
            User usuario
    ) {
        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente");
        }

        registrar(producto, cantidad, TipoMovimientoInventario.SALIDA);
    }

    @Transactional
    public void registrarAjuste(
            Producto producto,
            Integer stockReal
    ) {
        registrar(producto, stockReal, TipoMovimientoInventario.AJUSTE);
    }

    private void registrar(
            Producto producto,
            Integer cantidad,
            TipoMovimientoInventario tipo
    ) {
        MovimientoInventario m = new MovimientoInventario();

        m.setProducto(producto);
        m.setCantidad(cantidad);
        m.setTipoMovimiento(tipo);
        m.setFechaMovimiento(LocalDateTime.now());

        // Guardar stock anterior
        m.setStockAnterior(producto.getStock());

        // Actualizar stock según tipo
        int stockNuevo = switch (tipo) {
            case ENTRADA -> producto.getStock() + cantidad;
            case SALIDA -> producto.getStock() - cantidad;
            case AJUSTE -> cantidad;
        };

        // Guardar stock actual
        m.setStockActual(stockNuevo);

        // Actualizar producto
        producto.setStock(stockNuevo);

        repository.save(m);
        productoRepository.save(producto);
    }

    public PageResponse<MovimientoInventarioResponse> listarPorProducto(
            Long productoId, Pageable pageable
    ) {
        // Obtener la página de movimientos desde el repositorio
        Page<MovimientoInventario> page = repository.findByProductoId(productoId, pageable);

        // Mapear los movimientos a responses
        List<MovimientoInventarioResponse> content = page.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        // Construir y devolver el PageResponse
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
