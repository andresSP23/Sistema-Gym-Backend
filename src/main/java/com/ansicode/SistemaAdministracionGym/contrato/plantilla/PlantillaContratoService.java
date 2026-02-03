package com.ansicode.SistemaAdministracionGym.contrato.plantilla;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantillaContratoService {

    private final PlantillaContratoRepository repository;
    private final PlantillaContratoMapper mapper;

    @Transactional
    public PlantillaContratoResponse create(PlantillaContratoRequest request) {
        PlantillaContrato plantilla = mapper.toEntity(request);

        // Guardamos primero para tener ID
        plantilla = repository.save(plantilla);

        if (plantilla.isActivo()) {
            desactivarOtras(plantilla.getId());
        }
        return mapper.toResponse(plantilla);
    }

    @Transactional
    public PlantillaContratoResponse update(Long id, PlantillaContratoRequest request) {
        PlantillaContrato plantilla = repository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PLANTILLA_CONTRATO_NOT_FOUND));

        boolean wasActive = plantilla.isActivo();
        mapper.updateEntity(plantilla, request);

        if (request.isActivo() && !wasActive) {
            desactivarOtras(id);
        }

        return mapper.toResponse(repository.save(plantilla));
    }

    private void desactivarOtras(Long exceptId) {
        if (exceptId != null) {
            repository.deactivateAllExcept(exceptId);
        }
    }

    public PageResponse<PlantillaContratoResponse> findAll(Pageable pageable) {
        Page<PlantillaContrato> page = repository.findAll(pageable);
        List<PlantillaContratoResponse> content = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<PlantillaContratoResponse>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public PlantillaContratoResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.PLANTILLA_CONTRATO_NOT_FOUND));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BussinessException(BusinessErrorCodes.PLANTILLA_CONTRATO_NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
