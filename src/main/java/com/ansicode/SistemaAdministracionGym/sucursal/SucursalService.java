package com.ansicode.SistemaAdministracionGym.sucursal;

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

        // Solo una sucursal (no multisucursal)
        if (sucursalRepository.count() > 0) {
            throw new IllegalStateException("La sucursal ya está registrada");
        }

        if (!request.getHoraApertura().isBefore(request.getHoraCierre())) {
            throw new IllegalArgumentException(
                    "La hora de apertura debe ser menor a la hora de cierre"
            );
        }

        Sucursal sucursal = sucursalMapper.toEntity(request);
        sucursalRepository.save(sucursal);

        return sucursalMapper.toResponse(sucursal);
    }

    public SucursalResponse obtener() {
        Sucursal sucursal = sucursalRepository.findFirstByActivoTrue()
                .orElseThrow(() ->
                        new EntityNotFoundException("Sucursal no registrada")
                );

        return sucursalMapper.toResponse(sucursal);
    }




    @Transactional
    public SucursalResponse actualizar(SucursalRequest request) {
        // Obtener la sucursal actual
        Sucursal sucursal = sucursalRepository.findFirstByActivoTrue()
                .orElseThrow(() ->
                        new EntityNotFoundException("No hay sucursal registrada para actualizar")
                );

        // Validaciones
        if (!request.getHoraApertura().isBefore(request.getHoraCierre())) {
            throw new IllegalArgumentException(
                    "La hora de apertura debe ser menor a la hora de cierre"
            );
        }

        // Actualizar campos permitidos
        sucursal.setNombre(request.getNombre());
        // Código no se puede cambiar si quieres control
        // sucursal.setCodigoSucursal(request.getCodigoSucursal());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setCiudad(request.getCiudad());
        sucursal.setProvincia(request.getProvincia());
        sucursal.setPais(request.getPais());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setEmail(request.getEmail());
        sucursal.setHoraApertura(request.getHoraApertura());
        sucursal.setHoraCierre(request.getHoraCierre());
        sucursal.setFechaApertura(request.getFechaApertura());
        sucursal.setAforoMaximo(request.getAforoMaximo());
        sucursal.setRuc(request.getRuc());
        sucursal.setRazonSocial(request.getRazonSocial());
        sucursal.setLogoUrl(request.getLogoUrl());
        sucursal.setColorPrimario(request.getColorPrimario());

        sucursalRepository.save(sucursal);

        return sucursalMapper.toResponse(sucursal);
    }
}
