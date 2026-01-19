package com.ansicode.SistemaAdministracionGym.conteocaja;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ConteoCajaService {

    private final SesionCajaRepository sesionCajaRepository;
    private final ConteoCajaRepository conteoCajaRepository;

    @Transactional
    public void guardarConteo(GuardarConteoCajaRequest request) {

        SesionCaja sesion = sesionCajaRepository.findById(request.getSesionCajaId())
                .orElseThrow(() -> new EntityNotFoundException("Sesión de caja no encontrada"));

        if (sesion.getEstado() == EstadoSesionCaja.CERRADA) {
            throw new IllegalStateException("No puedes registrar conteo en una sesión CERRADA");
        }

        // Reemplaza conteo completo (más simple y sin duplicados)
        conteoCajaRepository.deleteBySesionCajaId(sesion.getId());

        for (ConteoCajaItemRequest item : request.getItems()) {
            BigDecimal denom = item.getDenominacion().setScale(2, RoundingMode.HALF_UP);
            int cant = item.getCantidad();

            BigDecimal subtotal = denom.multiply(BigDecimal.valueOf(cant))
                    .setScale(2, RoundingMode.HALF_UP);

            ConteoCaja c = ConteoCaja.builder()
                    .sesionCaja(sesion)
                    .moneda(item.getMoneda() == null ? "USD" : item.getMoneda().trim().toUpperCase())
                    .denominacion(denom)
                    .cantidad(cant)
                    .subtotal(subtotal)
                    .build();

            conteoCajaRepository.save(c);
        }
    }
}
