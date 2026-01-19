package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import com.ansicode.SistemaAdministracionGym.conteocaja.ConteoCajaRepository;
import com.ansicode.SistemaAdministracionGym.enums.EstadoCuadreCaja;
import com.ansicode.SistemaAdministracionGym.movimientodinero.MovimientoDineroRepository;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.convert.ReadingConverter;
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

    @Transactional
    public CuadreCaja generarCuadre(Long sesionCajaId, String moneda, String observacion) {

        SesionCaja sesion = sesionCajaRepository.findById(sesionCajaId)
                .orElseThrow(() -> new EntityNotFoundException("Sesión de caja no encontrada"));

        String mon = (moneda == null || moneda.isBlank()) ? "USD" : moneda.trim().toUpperCase();

        BigDecimal base = (sesion.getBaseInicialEfectivo() == null ? BigDecimal.ZERO : sesion.getBaseInicialEfectivo())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal netoEfectivo = movimientoDineroRepository.netoEfectivoPorSesion(sesionCajaId, mon);
        if (netoEfectivo == null) netoEfectivo = BigDecimal.ZERO;
        netoEfectivo = netoEfectivo.setScale(2, RoundingMode.HALF_UP);

        BigDecimal esperado = base.add(netoEfectivo).setScale(2, RoundingMode.HALF_UP);

        BigDecimal contado = conteoCajaRepository.totalContado(sesionCajaId, mon);
        if (contado == null) contado = BigDecimal.ZERO;
        contado = contado.setScale(2, RoundingMode.HALF_UP);

        BigDecimal diferencia = contado.subtract(esperado).setScale(2, RoundingMode.HALF_UP);

        CuadreCaja cuadre = cuadreCajaRepository.findBySesionCaja_Id(sesionCajaId)
                .orElseGet(() -> CuadreCaja.builder().sesionCaja(sesion).build());

        cuadre.setEfectivoEsperado(esperado);
        cuadre.setEfectivoContado(contado);
        cuadre.setDiferencia(diferencia);
        cuadre.setEstado(diferencia.compareTo(BigDecimal.ZERO) == 0 ? EstadoCuadreCaja.COMPLETO : EstadoCuadreCaja.PARCIAL);

        if (observacion != null && !observacion.isBlank()) {
            cuadre.setObservacion(observacion.length() > 300 ? observacion.substring(0, 300) : observacion);
        }

        return cuadreCajaRepository.save(cuadre);
    }
}
