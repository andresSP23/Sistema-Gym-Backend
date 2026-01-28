package com.ansicode.SistemaAdministracionGym.sucursal;

import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;

    public SucursalResponse registrar(SucursalRequest request) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }

        // Solo una sucursal (no multisucursal)
        if (sucursalRepository.count() > 0) {
            throw new BussinessException(BusinessErrorCodes.SUCURSAL_YA_REGISTRADA);
        }

        // Validación horas
        if (request.getHoraApertura() == null || request.getHoraCierre() == null) {
            throw new BussinessException(BusinessErrorCodes.SUCURSAL_HORARIO_REQUIRED);
        }
        if (!request.getHoraApertura().isBefore(request.getHoraCierre())) {
            throw new BussinessException(BusinessErrorCodes.SUCURSAL_HORARIO_INVALIDO);
        }

        Sucursal sucursal = sucursalMapper.toEntity(request);

        // si manejas “registro único”, asegúrate de dejar visible
        if (sucursal.getIsVisible() == null) {
            sucursal.setIsVisible(true);
        }

        sucursalRepository.save(sucursal);

        return sucursalMapper.toResponse(sucursal);
    }

    @Transactional(readOnly = true)
    public SucursalResponse obtener() {

        Sucursal sucursal = sucursalRepository.findFirstByIsVisibleTrue()
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SUCURSAL_NOT_FOUND));

        return sucursalMapper.toResponse(sucursal);
    }

    public SucursalResponse actualizar(SucursalRequest request) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }

        Sucursal sucursal = sucursalRepository.findFirstByIsVisibleTrue()
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SUCURSAL_NOT_FOUND_PARA_ACTUALIZAR));

        if (request.getHoraApertura() == null || request.getHoraCierre() == null) {
            throw new BussinessException(BusinessErrorCodes.SUCURSAL_HORARIO_REQUIRED);
        }
        if (!request.getHoraApertura().isBefore(request.getHoraCierre())) {
            throw new BussinessException(BusinessErrorCodes.SUCURSAL_HORARIO_INVALIDO);
        }

        // Actualizar campos permitidos
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setCiudad(request.getCiudad());
        sucursal.setProvincia(request.getProvincia());
        sucursal.setPais(request.getPais());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setEmail(request.getEmail());
        sucursal.setHoraApertura(request.getHoraApertura());
        sucursal.setHoraCierre(request.getHoraCierre());
        sucursal.setAforoMaximo(request.getAforoMaximo());
        sucursal.setRuc(request.getRuc());
        sucursal.setRazonSocial(request.getRazonSocial());
        sucursal.setLogoUrl(request.getLogoUrl());
        sucursal.setColorPrimario(request.getColorPrimario());

        sucursalRepository.save(sucursal);

        return sucursalMapper.toResponse(sucursal);
    }
}