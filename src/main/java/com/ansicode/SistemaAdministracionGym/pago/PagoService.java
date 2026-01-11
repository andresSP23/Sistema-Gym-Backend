package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
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

    @Transactional
    public PagoResponse create(PagoRequest request) {

        //  Buscar la membresía cliente
        MembresiaCliente mc = membresiaClienteRepository
                .findById(request.getMembresiaClienteId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Membresía del cliente no encontrada")
                );

        // 2️⃣ Validar estado
        if (!EstadoMembresia.PENDIENTE_PAGO.equals(mc.getEstado())) {
            throw new IllegalStateException(
                    "Solo se puede pagar una membresía pendiente de pago"
            );
        }

        // Validar monto (regla clave)
        if (request.getMonto().compareTo(mc.getMembresia().getPrecio()) < 0) {
            throw new IllegalArgumentException(
                    "El monto pagado es inferior al precio de la membresía"
            );
        }

        // Crear pago
        Pago pago = mapper.toPago(request, mc);
            pago.setActivo(true);

        repository.save(pago);

        //  Activar membresía (LÓGICA DE DOMINIO)
        mc.activar();

        membresiaClienteRepository.save(mc);

        // Respuesta
        return mapper.toPagoResponse(pago);
    }

    @Transactional
    public PagoResponse update(Long id, PagoRequest request) {

        Pago pago = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));

        MembresiaCliente mc = membresiaClienteRepository
                .findById(request.getMembresiaClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Membresía del cliente no encontrada"));

        // 🔒 Regla crítica
        if (EstadoMembresia.ACTIVA.equals(mc.getEstado())) {
            throw new IllegalStateException(
                    "No se puede modificar un pago que ya activó una membresía"
            );
        }

        mapper.updatePagoFromRequest(pago, request, mc);

        return mapper.toPagoResponse(pago);
    }


    @Transactional
    public void delete(Long id) {

        Pago pago = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));

        if (EstadoMembresia.ACTIVA.equals(
                pago.getMembresiaCliente().getEstado()
        )) {
            throw new IllegalStateException(
                    "No se puede eliminar un pago que activó una membresía"
            );
        }

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

        Page<Pago> page = repository.findByMembresiaClienteId(membresiaClienteId, pageable);

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
