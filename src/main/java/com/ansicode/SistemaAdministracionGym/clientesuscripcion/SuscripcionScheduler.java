package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSuscripcion;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuscripcionScheduler {

    private final ClienteSuscripcionRepository clienteSuscripcionRepository;


    @Scheduled(cron = "0 5 0 * * *") // diario 00:05
    @Transactional
    public void vencerSuscripciones() {
        LocalDateTime ahora = LocalDateTime.now();

        List<ClienteSuscripcion> vencidas =
                clienteSuscripcionRepository.findByEstadoAndFechaFinBefore(EstadoSuscripcion.ACTIVA, ahora);

        for (ClienteSuscripcion cs : vencidas) {
            cs.setEstado(EstadoSuscripcion.VENCIDA);
        }
    }
}
