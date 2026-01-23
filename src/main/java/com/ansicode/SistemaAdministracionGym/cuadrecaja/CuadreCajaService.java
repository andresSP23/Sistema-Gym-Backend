package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.conteocaja.ConteoCajaRepository;
import com.ansicode.SistemaAdministracionGym.enums.EstadoCuadreCaja;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroRepository;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CuadreCajaService {


    private final SesionCajaRepository sesionCajaRepository;
    private final ConteoCajaRepository conteoCajaRepository;
    private final CuadreCajaRepository cuadreCajaRepository;
    private final MovimientoDineroRepository movimientoDineroRepository;
    private final CuadreCajaMapper  cuadreCajaMapper;

    @Transactional
    public CuadreCajaResponse generarCuadre(Long sesionCajaId, GenerarCuadreRequest body) {

        String moneda = resolveMoneda(body);
        String observacion = (body != null) ? body.getObservacion() : null;

        SesionCaja sesion = sesionCajaRepository.findById(sesionCajaId)
                .orElseThrow(() -> new EntityNotFoundException("Sesión de caja no encontrada"));

        BigDecimal base = money(sesion.getBaseInicialEfectivo());

        BigDecimal netoEfectivo = money(
                movimientoDineroRepository.netoEfectivoPorSesion(sesionCajaId, moneda)
        );

        BigDecimal esperado = money(base.add(netoEfectivo));

        BigDecimal contado = money(
                conteoCajaRepository.totalContado(sesionCajaId, moneda)
        );

        BigDecimal diferencia = money(contado.subtract(esperado));

        CuadreCaja cuadre = cuadreCajaRepository.findBySesionCaja_Id(sesionCajaId)
                .orElseGet(() -> CuadreCaja.builder()
                        .sesionCaja(sesion)
                        .build());

        cuadre.setEfectivoEsperado(esperado);
        cuadre.setEfectivoContado(contado);
        cuadre.setDiferencia(diferencia);
        cuadre.setEstado(
                diferencia.compareTo(BigDecimal.ZERO) == 0
                        ? EstadoCuadreCaja.COMPLETO
                        : EstadoCuadreCaja.PARCIAL
        );

        // observación
        if (observacion == null || observacion.isBlank()) {
            cuadre.setObservacion(null);
        } else {
            cuadre.setObservacion(observacion.length() > 300
                    ? observacion.substring(0, 300)
                    : observacion);
        }

        CuadreCaja saved = cuadreCajaRepository.save(cuadre);

        return cuadreCajaMapper.toResponse(saved);
    }

    private String resolveMoneda(GenerarCuadreRequest body) {
        if (body == null) return "USD";
        if (body.getMoneda() == null) return "USD";
        if (body.getMoneda().isBlank()) return "USD";
        return body.getMoneda().trim().toUpperCase();
    }

    private BigDecimal money(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }


    @Transactional(readOnly = true)
    public PageResponse<CuadreCajaResponse> findAll(Pageable pageable) {

        Page<CuadreCaja> page = cuadreCajaRepository.findAll(pageable);

        return PageResponse.<CuadreCajaResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(cuadreCajaMapper::toResponse)
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

}


