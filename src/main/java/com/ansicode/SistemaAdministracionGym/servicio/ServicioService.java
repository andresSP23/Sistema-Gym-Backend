package com.ansicode.SistemaAdministracionGym.servicio;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServiciosRepository repository;
    private final ServicioMapper mapper;



    @Transactional
    public ServiciosResponse create(ServiciosRequest request) {
        Servicios s = mapper.toEntity(request);
        return mapper.toResponse(repository.save(s));
    }

    @Transactional
    public ServiciosResponse update(Long id, ServiciosRequest request) {
        Servicios s = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        mapper.mapToEntity(s, request);

        return mapper.toResponse(repository.save(s));
    }

    @Transactional(readOnly = true)
    public ServiciosResponse findById(Long id) {
        Servicios s = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        return mapper.toResponse(s);
    }

    public void delete(Long id) {

        Servicios servicios = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        repository.delete(servicios);
    }

    @Transactional(readOnly = true)
    public PageResponse<ServiciosResponse> list(Boolean suscripcion, Pageable pageable) {

        Page<Servicios> page = repository.findByEsSuscripcionAndEstado(suscripcion, true, pageable);

        return PageResponse.<ServiciosResponse>builder()
                .content(page.getContent().stream().map(mapper::toResponse).toList())
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ServiciosResponse> combo(Boolean suscripcion) {
        return repository.findByEsSuscripcionAndEstadoOrderByNombreAsc(suscripcion, true)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
