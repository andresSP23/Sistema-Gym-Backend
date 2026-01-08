package com.ansicode.SistemaAdministracionGym.membresia;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembresiaService {

    private final MembresiaRepository repository;
    private final MembresiaMapper mapper;


    @Transactional
    public MembresiaResponse create(MembresiaRequest request) {

        Membresia membresia = mapper.toMembresia(request);
        membresia.setActivo(true);

        repository.save(membresia);
        return mapper.toMembresiaResponse(membresia);
    }

    public PageResponse<MembresiaResponse> findAll(Pageable pageable) {

        Page<Membresia> page = repository.findAll(pageable);

        return PageResponse.<MembresiaResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(mapper::toMembresiaResponse)
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

    public MembresiaResponse findById(Long id) {

        Membresia membresia = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));

        return mapper.toMembresiaResponse(membresia);
    }

    @Transactional
    public MembresiaResponse update(Long id, MembresiaRequest request) {

        Membresia membresia = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));

        mapper.updateMembresiaFromRequest(membresia, request);

        return mapper.toMembresiaResponse(membresia);
    }

    @Transactional
    public void delete(Long id) {

        Membresia membresia = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));

        repository.delete(membresia);
    }
}
