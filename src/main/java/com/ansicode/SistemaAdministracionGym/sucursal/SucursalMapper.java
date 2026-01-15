package com.ansicode.SistemaAdministracionGym.sucursal;

import org.springframework.stereotype.Component;

@Component
public class SucursalMapper {

    public Sucursal toEntity(SucursalRequest request) {
        Sucursal sucursal = new Sucursal();

        sucursal.setNombre(request.getNombre());
        sucursal.setCodigoSucursal(request.getCodigoSucursal());
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

        sucursal.setActivo(true);

        return sucursal;
    }

    public SucursalResponse toResponse(Sucursal sucursal) {
        SucursalResponse response = new SucursalResponse();

        response.setId(sucursal.getId());
        response.setNombre(sucursal.getNombre());
        response.setCodigoSucursal(sucursal.getCodigoSucursal());
        response.setDireccion(sucursal.getDireccion());
        response.setCiudad(sucursal.getCiudad());
        response.setProvincia(sucursal.getProvincia());
        response.setPais(sucursal.getPais());
        response.setTelefono(sucursal.getTelefono());
        response.setEmail(sucursal.getEmail());
        response.setHoraApertura(sucursal.getHoraApertura());
        response.setHoraCierre(sucursal.getHoraCierre());
        response.setFechaApertura(sucursal.getFechaApertura());
        response.setAforoMaximo(sucursal.getAforoMaximo());
        response.setRuc(sucursal.getRuc());
        response.setRazonSocial(sucursal.getRazonSocial());
        response.setLogoUrl(sucursal.getLogoUrl());
        response.setColorPrimario(sucursal.getColorPrimario());
        response.setActivo(sucursal.getActivo());

        return response;
    }
}
