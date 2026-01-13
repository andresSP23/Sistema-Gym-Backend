package com.ansicode.SistemaAdministracionGym.asistencia;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.membresiacliente.MembresiaCliente;
import com.ansicode.SistemaAdministracionGym.membresiacliente.MembresiaClienteRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor

public class AsistenciaService {


    private final ClienteRepository clienteRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final MembresiaClienteRepository membresiaClienteRepository;
    private final PagoRepository pagoRepository;
    private final AsistenciaMapper asistenciaMapper;

    @Transactional
    public AsistenciaResponse registrarPorCedula(AsistenciaRequest request) {
        // Buscar cliente por cédula
        Cliente cliente = clienteRepository.findByCedula(request.getCedulaCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        // Evitar duplicados en el mismo día
        LocalDateTime inicioDia = request.getFechaEntrada().toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1).minusSeconds(1);

        if (asistenciaRepository.existsByClienteIdAndFechaEntradaBetween(
                cliente.getId(),
                inicioDia,
                finDia
        )) {
            throw new IllegalArgumentException("El cliente ya tiene asistencia registrada para este día");
        }

        // Registrar asistencia
        Asistencia asistencia = asistenciaMapper.toAsistencia(request, cliente);
        asistencia.setActivo(true);
        asistenciaRepository.save(asistencia);

        // Traer membresía activa si existe
        MembresiaCliente membresiaCliente = membresiaClienteRepository.findByClienteIdAndEstado(
                cliente.getId(),
                EstadoMembresia.ACTIVA
        ).orElse(null);

        // Traer pagos asociados a esa membresía usando pageable (aunque traigamos todos, size grande)
        List<Pago> pagos;
        if (membresiaCliente != null) {
            pagos = pagoRepository.findByMembresiaClienteId(
                    membresiaCliente.getId(),
                    Pageable.ofSize(1000) // límite alto para traer todos
            ).getContent();
        } else {
            pagos = Collections.emptyList();
        }

        long diasRestantes = 0;

        //CALCULAR DÍAS RESTANTES
        if (membresiaCliente != null) {

            if (membresiaCliente.getFechaFin().isBefore(LocalDate.now())) {
                throw new IllegalStateException("La membresía está vencida");
            }

             diasRestantes = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    membresiaCliente.getFechaFin()
            );

            membresiaCliente.setDiasRestantes(
                    Math.max(diasRestantes, 0)
            );
        }


        return asistenciaMapper.toAsistenciaResponse(asistencia, membresiaCliente, pagos , Math.max(diasRestantes, 0)
        );
    }

    public PageResponse<AsistenciaResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        Page<Asistencia> page = asistenciaRepository.findByClienteId(clienteId, pageable);

        List<AsistenciaResponse> content = page.getContent()
                .stream()
                .map(a -> {
                    MembresiaCliente mc = membresiaClienteRepository.findByClienteIdAndEstado(
                            a.getCliente().getId(), EstadoMembresia.ACTIVA).orElse(null);

                    List<Pago> pagos;
                    if (mc != null) {
                        pagos = pagoRepository.findByMembresiaClienteId(
                                mc.getId(),
                                Pageable.ofSize(1000)
                        ).getContent();
                    } else {
                        pagos = Collections.emptyList();
                    }

                    long diasRestantes = 0;
                    if (mc != null) {
                        diasRestantes = ChronoUnit.DAYS.between(
                                LocalDate.now(),
                                mc.getFechaFin()
                        );
                        diasRestantes = Math.max(diasRestantes, 0);
                    }

                    return asistenciaMapper.toAsistenciaResponse(a, mc, pagos , diasRestantes );
                })
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
