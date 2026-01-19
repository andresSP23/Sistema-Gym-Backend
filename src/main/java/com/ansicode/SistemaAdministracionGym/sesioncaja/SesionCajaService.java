package com.ansicode.SistemaAdministracionGym.sesioncaja;

import com.ansicode.SistemaAdministracionGym.cuadrecaja.CuadreCajaRepository;
import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import com.ansicode.SistemaAdministracionGym.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

        repository.findFirstBySucursalIdAndEstadoOrderByFechaAperturaDesc(request.getSucursalId(), EstadoSesionCaja.ABIERTA)
                .ifPresent(s -> {
                    throw new IllegalStateException("Ya existe una sesión de caja ABIERTA para esta sucursal");
                });

        User user =  ((User)  connectedUser.getPrincipal());
        SesionCaja s = SesionCaja.builder()
                .sucursalId(request.getSucursalId())
                .usuarioAperturaId(user.getId())
                .fechaApertura(LocalDateTime.now())
                .baseInicialEfectivo(request.getBaseInicialEfectivo())
                .estado(EstadoSesionCaja.ABIERTA)
                .observacion(request.getObservacion())
                .build();

        return mapper.toResponse(repository.save(s));
    }

    /**
     * Obtener sesión abierta: por sucursal.
     * Esto lo usan: MovimientoDineroService, Pago/VentaService, etc.
     */
    @Transactional(readOnly = true)
    public SesionCaja obtenerSesionAbiertaPorSucursal(Long sucursalId) {
        return repository.findFirstBySucursalIdAndEstadoOrderByFechaAperturaDesc(sucursalId, EstadoSesionCaja.ABIERTA)
                .orElseThrow(() -> new IllegalStateException("No hay sesión de caja ABIERTA para esta sucursal"));
    }

    /**
     * Alternativa: sesión abierta por usuario (si tú lo prefieres)
     */
    @Transactional(readOnly = true)
    public SesionCaja obtenerSesionAbiertaPorUsuario(Long usuarioId) {
        return repository.findFirstByUsuarioAperturaIdAndEstadoOrderByFechaAperturaDesc(usuarioId, EstadoSesionCaja.ABIERTA)
                .orElseThrow(() -> new IllegalStateException("No hay sesión de caja ABIERTA para este usuario"));
    }

    /**
     * Cerrar caja
     */
    @Transactional
    public SesionCajaResponse cerrarCaja(Long sesionCajaId, CerrarCajaRequest request, Authentication connectedUser) {

        SesionCaja s = repository.findById(sesionCajaId)
                .orElseThrow(() -> new EntityNotFoundException("Sesión de caja no encontrada"));

        if (s.getEstado() == EstadoSesionCaja.CERRADA) {
            throw new IllegalStateException("La sesión ya está CERRADA");
        }

        if (cuadreCajaRepository.findBySesionCaja_Id(s.getId()).isEmpty()) {
            throw new IllegalStateException("Debe realizar el cuadre antes de cerrar caja");
        }

        User user =  ((User)  connectedUser.getPrincipal());

        s.setEstado(EstadoSesionCaja.CERRADA);
        s.setFechaCierre(LocalDateTime.now());
        s.setUsuarioCierreId(user.getId());

        if (request.getObservacion() != null && !request.getObservacion().isBlank()) {
            s.setObservacion(request.getObservacion().length() > 300 ? request.getObservacion().substring(0, 300) : request.getObservacion());
        }

        return mapper.toResponse(repository.save(s));
    }
}
