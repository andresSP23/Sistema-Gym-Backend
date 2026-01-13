package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteResponse create(ClienteRequest request) {

        if (clienteRepository.existsByCedula(request.getCedula())) {
            throw new IllegalStateException("La cédula ya está registrada");
        }

        Cliente cliente = clienteMapper.toCliente(request);
        cliente.setActivo(true);
        cliente.setCodigoInterno(generarCodigoInterno());


        clienteRepository.save(cliente);

        return clienteMapper.toClienteResponse(cliente);
    }

    public PageResponse<ClienteResponse> findAll(Pageable pageable) {

        Page<Cliente> page = clienteRepository.findAll(pageable);

        return PageResponse.<ClienteResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(clienteMapper::toClienteResponse)
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

    public ClienteResponse findById(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        return clienteMapper.toClienteResponse(cliente);
    }

    public ClienteResponse findByCedula(String cedula) {

        Cliente cliente = clienteRepository.findByCedula(cedula)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        return clienteMapper.toClienteResponse(cliente);
    }

    @Transactional
    public ClienteResponse update(Long id, ClienteRequest request) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        if (!cliente.getCedula().equals(request.getCedula())
                && clienteRepository.existsByCedula(request.getCedula())) {
            throw new IllegalStateException("La cédula ya está registrada");
        }

        clienteMapper.updateClienteFromRequest(cliente, request);

        return clienteMapper.toClienteResponse(cliente);
    }

    public void delete(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        clienteRepository.delete(cliente);
    }


    private String generarCodigoInterno() {
        long totalClientes = clienteRepository.count() + 1;
        int year = LocalDate.now().getYear();

        return String.format("CLI-%d-%06d", year, totalClientes);
    }

}
