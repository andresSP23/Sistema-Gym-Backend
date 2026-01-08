package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.membresiacliente.MembresiaCliente;
import com.ansicode.SistemaAdministracionGym.membresiacliente.MembresiaClienteRepository;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import com.ansicode.SistemaAdministracionGym.venta.VentaRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PagoService {


    private final PagoRepository repository;
    private final PagoMapper mapper;
    private final MembresiaClienteRepository membresiaClienteRepository;

    public PagoResponse create(PagoRequest request) {

        MembresiaCliente membresiaCliente = membresiaClienteRepository
                .findById(request.getMembresiaClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Membresía del cliente no encontrada"));

        Pago pago = mapper.toPago(request , membresiaCliente); // venta = null
        pago.setActivo(true);
        repository.save(pago);

        return mapper.toPagoResponse(pago);
    }

    public PagoResponse update(Long id, PagoRequest request) {

        Pago pago = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));

        MembresiaCliente membresiaCliente = membresiaClienteRepository
                .findById(request.getMembresiaClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Membresía del cliente no encontrada"));

        mapper.updatePagoFromRequest(pago, request, membresiaCliente); // venta = null

        return mapper.toPagoResponse(pago);
    }

    @Transactional
    public void delete(Long id) {

        Pago pago = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));

        repository.delete(pago);
    }

    public PagoResponse findById(Long id) {

        Pago pago = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));

        return mapper.toPagoResponse(pago);
    }

    public PageResponse<PagoResponse> findAll(Pageable pageable) {

        Page<Pago> page = repository.findAll(pageable);

        return PageResponse.<PagoResponse>builder()
                .content(page.getContent().stream()
                        .map(mapper::toPagoResponse)
                        .toList())
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public PageResponse<PagoResponse> findByMembresiaCliente(Long membresiaClienteId, Pageable pageable) {

        Page<Pago> page = repository.findByMembresiaClienteId(membresiaClienteId.intValue(), pageable);

        return PageResponse.<PagoResponse>builder()
                .content(page.getContent().stream()
                        .map(mapper::toPagoResponse)
                        .toList())
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }


}
