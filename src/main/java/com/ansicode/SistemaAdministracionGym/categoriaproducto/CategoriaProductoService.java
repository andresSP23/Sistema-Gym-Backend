package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;
    private final CategoriaProductoMapper categoriaProductoMapper;

    @Transactional
    public CategoriaProductoResponse create(CategoriaProductoRequest request) {

        if (categoriaProductoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new BussinessException(BusinessErrorCodes.CATEGORIA_PRODUCTO_ALREADY_EXISTS);
        }

        CategoriaProducto categoria = categoriaProductoMapper.toEntity(request);
        categoria.setIsVisible(true);

        categoria = categoriaProductoRepository.save(categoria);

        return categoriaProductoMapper.toResponse(categoria);
    }

    public PageResponse<CategoriaProductoResponse> findAll(Pageable pageable) {

        Page<CategoriaProducto> page = categoriaProductoRepository.findAll(pageable);

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
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CATEGORIA_PRODUCTO_NOT_FOUND));

        return categoriaProductoMapper.toResponse(categoria);
    }

    @Transactional
    public CategoriaProductoResponse update(Long id, CategoriaProductoRequest request) {

        CategoriaProducto categoria = categoriaProductoRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CATEGORIA_PRODUCTO_NOT_FOUND));

        if (!categoria.getNombre().equalsIgnoreCase(request.getNombre())
                && categoriaProductoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new BussinessException(BusinessErrorCodes.CATEGORIA_PRODUCTO_ALREADY_EXISTS);
        }

        categoriaProductoMapper.updateEntity(categoria, request);

        categoria = categoriaProductoRepository.save(categoria);

        return categoriaProductoMapper.toResponse(categoria);
    }

    @Transactional
    public void delete(Long id) {

        CategoriaProducto categoria = categoriaProductoRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CATEGORIA_PRODUCTO_NOT_FOUND));

        try {
            categoriaProductoRepository.delete(categoria);
        } catch (DataIntegrityViolationException e) {
            // si hay FK (productos asociados)
            throw new BussinessException(BusinessErrorCodes.CATEGORIA_PRODUCTO_DELETE_NOT_ALLOWED);
        }
    }
}