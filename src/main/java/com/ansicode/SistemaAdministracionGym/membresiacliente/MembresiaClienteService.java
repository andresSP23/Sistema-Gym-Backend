package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.membresia.Membresia;
import com.ansicode.SistemaAdministracionGym.membresia.MembresiaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembresiaClienteService {

    private final MembresiaClienteRepository repository;
    private final ClienteRepository clienteRepository;
    private final MembresiaRepository membresiaRepository;
    private final MembresiaClienteMapper mapper;

    @Transactional
    public MembresiaClienteResponse create(MembresiaClienteRequest request) {

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Membresia membresia = membresiaRepository.findById(request.getMembresiaId())
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));

        boolean existe = !repository.findByClienteIdAndEstadoIn(
                cliente.getId(),
                List.of(EstadoMembresia.PENDIENTE_PAGO, EstadoMembresia.ACTIVA)
        ).isEmpty();

        if (existe) {
            throw new IllegalArgumentException(
                    "El cliente ya tiene una membresía activa o pendiente de pago"
            );
        }

        MembresiaCliente mc = mapper.toNewEntity(cliente, membresia);
        repository.save(mc);

        return mapper.toResponse(mc);
    }

    /* =====================
       UPDATE ASIGNACIÓN
       ===================== */
    @Transactional
    public MembresiaClienteResponse update(Long id, MembresiaClienteRequest request) {

        MembresiaCliente mc = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Membresía del cliente no encontrada")
                );

        if (!mc.puedeEditarAsignacion()) {
            throw new IllegalArgumentException(
                    "Solo se puede editar una membresía pendiente de pago"
            );
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Membresia membresia = membresiaRepository.findById(request.getMembresiaId())
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));

        mapper.updateAsignacion(mc, cliente, membresia);

        return mapper.toResponse(mc);
    }

    /* =====================
       DELETE LÓGICO
       ===================== */
    @Transactional
    public void delete(Long id) {

        MembresiaCliente mc = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Membresía del cliente no encontrada")
                );

        if (!EstadoMembresia.PENDIENTE_PAGO.equals(mc.getEstado())) {
            throw new IllegalStateException(
                    "Solo se puede cancelar una membresía pendiente de pago"
            );
        }

        mc.setEstado(EstadoMembresia.CANCELADA);
        repository.delete(mc);
    }
    /* =====================
       FIND ALL (PAGINADO)
       ===================== */
    public PageResponse<MembresiaClienteResponse> findAll(Pageable pageable) {

        Page<MembresiaCliente> page = repository.findAll(pageable);

        return new PageResponse<>(
                page.getContent().stream()
                        .map(mapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
    /* =====================
       FIND BY ESTADO
       ===================== */
    public PageResponse<MembresiaClienteResponse> findByEstado(
            EstadoMembresia estado,
            Pageable pageable
    ) {

        Page<MembresiaCliente> page = repository.findByEstado(estado, pageable);

        return new PageResponse<>(
                page.getContent().stream()
                        .map(mapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    /* =====================
       ASISTENCIAS (NO FALLA)
       ===================== */
    public MembresiaCliente obtenerMembresiaActivaPorCliente(Long clienteId) {

        MembresiaCliente mc = repository.findByClienteIdAndEstado(
                clienteId,
                EstadoMembresia.ACTIVA
        ).orElseThrow(() ->
                new IllegalStateException("El cliente no tiene una membresía activa")
        );

        if (mc.getFechaFin().isBefore(LocalDate.now())) {
            throw new IllegalStateException("La membresía está vencida");
        }

        return mc;
    }

    /* =====================
       VENCIMIENTOS
       ===================== */
    @Transactional
    public void marcarMembresiasVencidas(LocalDate fechaActual) {

        List<MembresiaCliente> vencidas =
                repository.findByEstadoAndFechaFinBefore(
                        EstadoMembresia.ACTIVA,
                        fechaActual
                );

        if (vencidas.isEmpty()) return;

        for (MembresiaCliente mc : vencidas) {
            mc.setEstado(EstadoMembresia.VENCIDA);
        }
    }

}
