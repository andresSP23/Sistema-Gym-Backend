package com.ansicode.SistemaAdministracionGym.sesioncaja;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.cuadrecaja.CuadreCajaRepository;
import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SesionCajaService {

    private final SesionCajaRepository repository;
    private final SesionCajaMapper mapper;
    private final CuadreCajaRepository cuadreCajaRepository;

    @Transactional
    public SesionCajaResponse abrirCaja(AbrirCajaRequest request, Authentication connectedUser) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getSucursalId() == null) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_SUCURSAL_REQUIRED);
        }
        if (request.getBaseInicialEfectivo() == null) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_BASE_INICIAL_REQUIRED);
        }
        if (request.getBaseInicialEfectivo().signum() < 0) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_BASE_INICIAL_INVALIDA);
        }
        if (connectedUser == null || connectedUser.getPrincipal() == null) {
            throw new BussinessException(BusinessErrorCodes.BAD_CREDENTIALS);
        }

        // no puede haber otra abierta en esa sucursal
        repository.findFirstBySucursalIdAndEstadoOrderByFechaAperturaDesc(
                request.getSucursalId(),
                EstadoSesionCaja.ABIERTA
        ).ifPresent(s -> {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_YA_ABIERTA_SUCURSAL);
        });

        User user = (User) connectedUser.getPrincipal();

        SesionCaja s = SesionCaja.builder()
                .sucursalId(request.getSucursalId())
                .usuarioAperturaId(user.getId())
                .fechaApertura(LocalDateTime.now())
                .baseInicialEfectivo(request.getBaseInicialEfectivo())
                .estado(EstadoSesionCaja.ABIERTA)
                .observacion(trimObs(request.getObservacion()))
                .build();

        return mapper.toResponse(repository.save(s));
    }

    @Transactional(readOnly = true)
    public SesionCaja obtenerSesionAbiertaPorSucursal(Long sucursalId) {

        if (sucursalId == null) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_SUCURSAL_REQUIRED);
        }

        return repository.findFirstBySucursalIdAndEstadoOrderByFechaAperturaDesc(
                sucursalId,
                EstadoSesionCaja.ABIERTA
        ).orElseThrow(() -> new BussinessException(BusinessErrorCodes.SESION_CAJA_NO_ABIERTA_SUCURSAL));
    }

    @Transactional(readOnly = true)
    public SesionCaja obtenerSesionAbiertaPorUsuario(Long usuarioId) {

        if (usuarioId == null) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_USUARIO_REQUIRED);
        }

        return repository.findFirstByUsuarioAperturaIdAndEstadoOrderByFechaAperturaDesc(
                usuarioId,
                EstadoSesionCaja.ABIERTA
        ).orElseThrow(() -> new BussinessException(BusinessErrorCodes.SESION_CAJA_NO_ABIERTA_USUARIO));
    }

    @Transactional
    public SesionCajaResponse cerrarCaja(Long sesionCajaId, CerrarCajaRequest request, Authentication connectedUser) {

        if (sesionCajaId == null) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_ID_REQUIRED);
        }
        if (connectedUser == null || connectedUser.getPrincipal() == null) {
            throw new BussinessException(BusinessErrorCodes.BAD_CREDENTIALS);
        }

        SesionCaja s = repository.findById(sesionCajaId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SESION_CAJA_NOT_FOUND));

        if (s.getEstado() == EstadoSesionCaja.CERRADA) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_YA_CERRADA);
        }

        // debe existir cuadre para cerrar
        if (cuadreCajaRepository.findBySesionCaja_Id(s.getId()).isEmpty()) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_REQUIERE_CUADRE);
        }

        User user = (User) connectedUser.getPrincipal();

        s.setEstado(EstadoSesionCaja.CERRADA);
        s.setFechaCierre(LocalDateTime.now());
        s.setUsuarioCierreId(user.getId());
        s.setObservacion(trimObs(request != null ? request.getObservacion() : null));

        return mapper.toResponse(repository.save(s));
    }

    @Transactional(readOnly = true)
    public PageResponse<SesionCajaResponse> findAll(Pageable pageable) {

        Page<SesionCajaResponse> page = repository.findAllWithSaldoFinal(pageable);

        return PageResponse.<SesionCajaResponse>builder()
                .content(page.getContent())
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private String trimObs(String obs) {
        if (obs == null) return null;
        String o = obs.trim();
        if (o.isBlank()) return null;
        return o.length() > 300 ? o.substring(0, 300) : o;
    }
}
