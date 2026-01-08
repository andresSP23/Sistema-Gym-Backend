package com.ansicode.SistemaAdministracionGym.producto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;



    @Transactional
    public ProductoResponse create(ProductoRequest request) {
        Producto producto = mapper.toProducto(request);
        producto.setActivo(true);
        repository.save(producto);
        return mapper.toProductoResponse(producto);
    }

    public Page<ProductoResponse> findAll(Pageable pageable) {
        Page<Producto> page = repository.findAll(pageable);
        return page.map(mapper::toProductoResponse);
    }

    public Page<ProductoResponse> findByStockGreaterThan(Integer stock, Pageable pageable) {
        Page<Producto> page = repository.findByStockGreaterThan(stock, pageable);
        return page.map(mapper::toProductoResponse);
    }

    public ProductoResponse findById(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        return mapper.toProductoResponse(producto);
    }

    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        mapper.updateProducto(producto, request);
        return mapper.toProductoResponse(producto);
    }

    public void delete(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        repository.delete(producto);
    }
}
