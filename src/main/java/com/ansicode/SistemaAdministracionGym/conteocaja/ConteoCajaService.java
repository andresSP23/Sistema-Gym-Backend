package com.ansicode.SistemaAdministracionGym.conteocaja;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ConteoCajaService {

    private final SesionCajaRepository sesionCajaRepository;
    private final ConteoCajaRepository conteoCajaRepository;
    private final ConteoCajaMapper conteoCajaMapper;

    @Transactional
    public ConteoCajaResponse guardarConteo(GuardarConteoCajaRequest request) {

        SesionCaja sesion = sesionCajaRepository.findById(request.getSesionCajaId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.SESION_CAJA_NOT_FOUND));

        if (sesion.getEstado() == EstadoSesionCaja.CERRADA) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_CERRADA);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BussinessException(BusinessErrorCodes.CONTEO_CAJA_ITEMS_REQUIRED);
        }

        // Reemplaza conteo completo (sin duplicados)
        conteoCajaRepository.deleteBySesionCajaId(sesion.getId());

        for (ConteoCajaItemRequest item : request.getItems()) {

            if (item == null) {
                throw new BussinessException(BusinessErrorCodes.CONTEO_CAJA_ITEM_INVALIDO);
            }

            String moneda = normalizeMoneda(item.getMoneda());
            BigDecimal denom = money(item.getDenominacion());

            if (denom.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BussinessException(BusinessErrorCodes.CONTEO_CAJA_DENOMINACION_INVALIDA);
            }

            int cant = item.getCantidad() == null ? 0 : item.getCantidad();
            if (cant < 0) {
                throw new BussinessException(BusinessErrorCodes.CONTEO_CAJA_CANTIDAD_INVALIDA);
            }

            BigDecimal subtotal = money(denom.multiply(BigDecimal.valueOf(cant)));

            ConteoCaja c = ConteoCaja.builder()
                    .sesionCaja(sesion)
                    .moneda(moneda)
                    .denominacion(denom)
                    .cantidad(cant)
                    .subtotal(subtotal)
                    .build();

            conteoCajaRepository.save(c);
        }

        List<ConteoCaja> items = conteoCajaRepository
                .findBySesionCaja_IdOrderByDenominacionDesc(sesion.getId());

        return conteoCajaMapper.toResponse(sesion.getId(), items);
    }

    @Transactional(readOnly = true)
    public ConteoCajaResponse obtenerPorSesion(Long sesionCajaId) {

        if (!sesionCajaRepository.existsById(sesionCajaId)) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_NOT_FOUND);
        }

        List<ConteoCaja> items = conteoCajaRepository
                .findBySesionCaja_IdOrderByDenominacionDesc(sesionCajaId);

        return conteoCajaMapper.toResponse(sesionCajaId, items);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalContado(Long sesionCajaId, String moneda) {

        if (!sesionCajaRepository.existsById(sesionCajaId)) {
            throw new BussinessException(BusinessErrorCodes.SESION_CAJA_NOT_FOUND);
        }

        return money(conteoCajaRepository.totalContado(
                sesionCajaId,
                normalizeMonedaNullable(moneda)
        ));
    }

    private String normalizeMoneda(String moneda) {
        return (moneda == null || moneda.isBlank()) ? "USD" : moneda.trim().toUpperCase();
    }

    private String normalizeMonedaNullable(String moneda) {
        if (moneda == null || moneda.isBlank()) return null;
        return moneda.trim().toUpperCase();
    }

    private BigDecimal money(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }
}

