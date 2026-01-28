package com.ansicode.SistemaAdministracionGym.asistencia;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.clientesuscripcion.ClienteSuscripcion;
import com.ansicode.SistemaAdministracionGym.clientesuscripcion.ClienteSuscripcionRepository;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSuscripcion;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import com.ansicode.SistemaAdministracionGym.pago.PagoRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor

public class AsistenciaService {


    private final ClienteRepository clienteRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final AsistenciaMapper asistenciaMapper;
    private final ClienteSuscripcionRepository  clienteSuscripcionRepository;

    @Transactional
    public AsistenciaResponse registrarPorCedula(AsistenciaRequest request) {

        Cliente cliente = clienteRepository.findByCedula(request.getCedulaCliente())
                .orElseThrow(() -> new BussinessException(
                        BusinessErrorCodes.ASISTENCIA_CLIENTE_NOT_FOUND));

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX);

        if (asistenciaRepository.existsByClienteIdAndFechaEntradaBetween(
                cliente.getId(), inicioDia, finDia)) {

            throw new BussinessException(
                    BusinessErrorCodes.ASISTENCIA_DUPLICADA_HOY);
        }

        LocalDateTime ahora = LocalDateTime.now();

        ClienteSuscripcion suscripcion = clienteSuscripcionRepository
                .findTopByClienteIdAndEstadoAndFechaFinAfterOrderByFechaFinDesc(
                        cliente.getId(),
                        EstadoSuscripcion.ACTIVA,
                        ahora
                )
                .orElseThrow(() -> new BussinessException(
                        BusinessErrorCodes.ASISTENCIA_SUSCRIPCION_NO_ACTIVA));

        Asistencia asistencia = new Asistencia();
        asistencia.setCliente(cliente);
        asistencia.setFechaEntrada(ahora);

        asistenciaRepository.save(asistencia);

        long diasRestantes = ChronoUnit.DAYS.between(
                ahora.toLocalDate(),
                suscripcion.getFechaFin().toLocalDate());
        diasRestantes = Math.max(diasRestantes, 0);

        return asistenciaMapper.toAsistenciaResponse(
                asistencia,
                suscripcion,
                Collections.emptyList(),
                diasRestantes
        );
    }


    public PageResponse<AsistenciaResponse> listarPorCliente(Long clienteId, Pageable pageable) {

        Page<Asistencia> page = asistenciaRepository.findByClienteId(clienteId, pageable);

        LocalDateTime ahora = LocalDateTime.now();

        // Traemos 1 vez la suscripción vigente (si existe)
        ClienteSuscripcion suscripcionVigente = clienteSuscripcionRepository
                .findTopByClienteIdAndEstadoAndFechaFinAfterOrderByFechaFinDesc(
                        clienteId,
                        EstadoSuscripcion.ACTIVA,
                        ahora
                )
                .orElse(null);

        long dias = 0;

        if (suscripcionVigente != null) {
            dias = ChronoUnit.DAYS.between(
                    ahora.toLocalDate(),
                    suscripcionVigente.getFechaFin().toLocalDate()
            );
            dias = Math.max(dias, 0);
        }

        final long diasFinal = dias;

        List<AsistenciaResponse> content = page.getContent()
                .stream()
                .map(a -> asistenciaMapper.toAsistenciaResponse(
                        a,
                        suscripcionVigente,
                        Collections.emptyList(),
                        diasFinal   // usar la final
                ))
                .toList();

        return PageResponse.<AsistenciaResponse>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
