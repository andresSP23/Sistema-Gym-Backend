package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    public ClienteResponse create(ClienteRequest request) {

        // 1) mapear
        Cliente cliente = clienteMapper.toCliente(request);

        // 2) guardar 1 vez para obtener ID (codigoInterno temporalmente null)
        cliente.setCodigoInterno(null);
        cliente.setIsVisible(true);
        cliente = clienteRepository.save(cliente);

        // 3) generar codigo interno con ID (único)
        int year = LocalDate.now().getYear();
        String correlativo = String.format("%06d", cliente.getId());
        cliente.setCodigoInterno("CLI-" + year + "-" + correlativo);

        // 4) guardar 2da vez ya con el código
        cliente = clienteRepository.save(cliente);

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

//    public void activarCliente (Long clienteId){
//
//
//        Cliente cliente = clienteRepository.findById(clienteId)
//                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
//
//         if(cliente.getEstado() == EstadoMembresia.ACTIVO){
//             return;
//         }
//         cliente.setEstado(EstadoMembresia.ACTIVO);
//         clienteRepository.save(cliente);
//    }

}
