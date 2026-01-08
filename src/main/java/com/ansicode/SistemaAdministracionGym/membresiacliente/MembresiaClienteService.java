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

@Service
@RequiredArgsConstructor
public class MembresiaClienteService {

    private final MembresiaClienteRepository repository;
    private final ClienteRepository clienteRepository;
    private final MembresiaRepository membresiaRepository;
    private final MembresiaClienteMapper mapper;

    @Transactional
    public MembresiaClienteResponse create(MembresiaClienteRequest request ) {

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Membresia membresia = membresiaRepository.findById(request.getMembresiaId())
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));

        MembresiaCliente membresiaCliente =
                mapper.toMembresiaCliente(request, cliente, membresia);

        membresiaCliente.setActivo(true);

        repository.save(membresiaCliente);
        return mapper.toMembresiaClienteResponse(membresiaCliente);
    }

    public PageResponse<MembresiaClienteResponse> findAll(Pageable pageable) {

        Page<MembresiaCliente> page = repository.findAll(pageable);

        return PageResponse.<MembresiaClienteResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(mapper::toMembresiaClienteResponse)
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

    public PageResponse<MembresiaClienteResponse> findByEstado(
            EstadoMembresia estado,
            Pageable pageable
    ) {

        Page<MembresiaCliente> page =
                repository.findByEstado(estado, pageable);

        return PageResponse.<MembresiaClienteResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(mapper::toMembresiaClienteResponse)
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

    public MembresiaClienteResponse findById(Long id) {

        MembresiaCliente membresiaCliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía del cliente no encontrada"));

        return mapper.toMembresiaClienteResponse(membresiaCliente);
    }

    @Transactional
    public MembresiaClienteResponse update(Long id, MembresiaClienteRequest request) {

        MembresiaCliente membresiaCliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía del cliente no encontrada"));

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Membresia membresia = membresiaRepository.findById(request.getMembresiaId())
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));

        mapper.updateMembresiaClienteFromRequest(
                membresiaCliente,
                request,
                cliente,
                membresia
        );

        return mapper.toMembresiaClienteResponse(membresiaCliente);
    }

    @Transactional
    public void delete(Long id) {

        MembresiaCliente membresiaCliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía del cliente no encontrada"));

        repository.delete(membresiaCliente);
    }
}
