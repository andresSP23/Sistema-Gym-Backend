package com.ansicode.SistemaAdministracionGym.servicio;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;



@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServiciosRepository repository;
    private final ServicioMapper mapper;

    @Transactional
    public ServiciosResponse create(ServiciosRequest request) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new BussinessException(BusinessErrorCodes.SERVICIO_NOMBRE_REQUIRED);
        }
        if (request.getPrecio() == null || request.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.SERVICIO_PRECIO_INVALIDO);
        }

        // si es suscripción, duracionDias debe ser válida
        if (Boolean.TRUE.equals(request.getEsSuscripcion())) {
            if (request.getDuracionDias() == null || request.getDuracionDias() < 1) {
                throw new BussinessException(BusinessErrorCodes.SERVICIO_DURACION_REQUIRED_PARA_SUSCRIPCION);
            }
        }

        Servicios s = mapper.toEntity(request);
        s.setEstado(true); // asumo que manejas estado boolean, si no, quítalo

        return mapper.toResponse(repository.save(s));
    }

    @Transactional
    public ServiciosResponse update(Long id, ServiciosRequest request) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new BussinessException(BusinessErrorCodes.SERVICIO_NOMBRE_REQUIRED);
        }
        if (request.getPrecio() == null || request.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.SERVICIO_PRECIO_INVALIDO);
        }

        if (Boolean.TRUE.equals(request.getEsSuscripcion())) {
            if (request.getDuracionDias() == null || request.getDuracionDias() < 1) {
                throw new BussinessException(BusinessErrorCodes.SERVICIO_DURACION_REQUIRED_PARA_SUSCRIPCION);
            }
        }

        Servicios s = repository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SERVICIO_NOT_FOUND));

        mapper.mapToEntity(s, request);

        return mapper.toResponse(repository.save(s));
    }

    @Transactional(readOnly = true)
    public ServiciosResponse findById(Long id) {

        Servicios s = repository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SERVICIO_NOT_FOUND));

        return mapper.toResponse(s);
    }

    @Transactional
    public void delete(Long id) {

        Servicios servicios = repository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SERVICIO_NOT_FOUND));

        repository.delete(servicios);
    }

    @Transactional(readOnly = true)
    public PageResponse<ServiciosResponse> list(Boolean suscripcion, Pageable pageable) {

        // si quieres permitir null => trae todo
        // aquí lo dejo estricto como tú lo tienes (para no cambiar contrato)
        if (suscripcion == null) {
            throw new BussinessException(BusinessErrorCodes.SERVICIO_FILTRO_SUSCRIPCION_REQUIRED);
        }

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
    public java.util.List<ServiciosResponse> combo(Boolean suscripcion) {

        if (suscripcion == null) {
            throw new BussinessException(BusinessErrorCodes.SERVICIO_FILTRO_SUSCRIPCION_REQUIRED);
        }

        return repository.findByEsSuscripcionAndEstadoOrderByNombreAsc(suscripcion, true)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}