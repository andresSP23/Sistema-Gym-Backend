package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipamientoService {

    private final EquipamientoRepository equipamientoRepository;
    private final EquipamientoMapper equipamientoMapper;

    @Transactional
    public EquipamientoResponse create(EquipamientoRequest request) {
        Equipamiento equipamiento = equipamientoMapper.toEquipamiento(request);
        return equipamientoMapper.toEquipamientoResponse(equipamientoRepository.save(equipamiento));
    }

    @Transactional
    public EquipamientoResponse update(Long id, EquipamientoRequest request) {
        Equipamiento equipamiento = equipamientoRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.EQUIPAMIENTO_NOT_FOUND)); // Assuming you
                                                                                                       // have this code
                                                                                                       // or use generic

        // Manual update or mapper update
        equipamiento.setNombre(request.getNombre());
        equipamiento.setUbicacion(request.getUbicacion());
        equipamiento.setEstadoEquipamiento(request.getEstadoEquipamiento());
        equipamiento.setFotoUrl(request.getFotoUrl());

        return equipamientoMapper.toEquipamientoResponse(equipamientoRepository.save(equipamiento));
    }

    public EquipamientoResponse findById(Long id) {
        return equipamientoRepository.findById(id)
                .map(equipamientoMapper::toEquipamientoResponse)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.EQUIPAMIENTO_NOT_FOUND));
    }

    public PageResponse<EquipamientoResponse> findAll(Pageable pageable) {
        Page<Equipamiento> page = equipamientoRepository.findAll(pageable);
        List<EquipamientoResponse> content = page.getContent().stream()
                .map(equipamientoMapper::toEquipamientoResponse)
                .toList();

        return PageResponse.<EquipamientoResponse>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional
    public void delete(Long id) {
        if (!equipamientoRepository.existsById(id)) {
            throw new BussinessException(BusinessErrorCodes.EQUIPAMIENTO_NOT_FOUND);
        }
        equipamientoRepository.deleteById(id);
    }
}
