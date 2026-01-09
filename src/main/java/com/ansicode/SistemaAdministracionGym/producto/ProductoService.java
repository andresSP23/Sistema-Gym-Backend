package com.ansicode.SistemaAdministracionGym.producto;

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
    private final ProductoRepository repository;
    private final ProductoMapper mapper;
    private final MovimientoInventarioService movimientoService;

    @Transactional
    public ProductoResponse create(
            ProductoRequest request,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        Producto producto = mapper.toEntity(request);
        producto.setActivo(true);

        repository.save(producto);

        if (request.getStockInicial() != null && request.getStockInicial() > 0) {
            movimientoService.registrarEntrada(
                    producto,
                    request.getStockInicial(),
                    user
            );
        }

        return mapper.toResponse(producto);
    }

    public PageResponse<ProductoResponse> findAll(Pageable pageable) {

        Page<Producto> page = repository.findAll(pageable);

        return PageResponse.<ProductoResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(mapper::toResponse)
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
        Producto producto = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );
        return mapper.toResponse(producto);
    }

    @Transactional
    public ProductoResponse update(
            Long id,
            ProductoRequest request
    ) {
        Producto producto = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

        mapper.updateEntity(producto, request);
        repository.save(producto);

        return mapper.toResponse(producto);
    }

    @Transactional
    public void delete(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

         repository.delete(producto);
    }


    @Transactional
    public void agregarStock(
            Long productoId,
            Integer cantidad,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        Producto producto = repository.findById(productoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

        movimientoService.registrarEntrada(producto, cantidad, user);
    }

    @Transactional
    public void ajustarStock(
            Long productoId,
            Integer stockReal,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        Producto producto = repository.findById(productoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Producto no encontrado")
                );

        movimientoService.registrarAjuste(producto, stockReal, user);
    }
}
