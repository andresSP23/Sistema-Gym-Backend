package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;
    private final CategoriaProductoMapper categoriaProductoMapper;

    public CategoriaProductoResponse create(CategoriaProductoRequest request) {

        CategoriaProducto categoria = categoriaProductoMapper.toEntity(request);
        categoria.setIsVisible(true);

        categoriaProductoRepository.save(categoria);

        return categoriaProductoMapper.toResponse(categoria);
    }

    public PageResponse<CategoriaProductoResponse> findAll(Pageable pageable) {

        Page<CategoriaProducto> page =
                categoriaProductoRepository.findAll(pageable);

        return PageResponse.<CategoriaProductoResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(categoriaProductoMapper::toResponse)
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

    public CategoriaProductoResponse findById(Long id) {

        CategoriaProducto categoria = categoriaProductoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoría no encontrada")
                );

        return categoriaProductoMapper.toResponse(categoria);
    }

    @Transactional
    public CategoriaProductoResponse update(
            Long id,
            CategoriaProductoRequest request
    ) {

        CategoriaProducto categoria = categoriaProductoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoría no encontrada")
                );

        categoriaProductoMapper.updateEntity(categoria, request);

        return categoriaProductoMapper.toResponse(categoria);
    }

    public void delete(Long id) {

        CategoriaProducto categoria = categoriaProductoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoría no encontrada")
                );

        categoriaProductoRepository.delete(categoria);
    }
}
