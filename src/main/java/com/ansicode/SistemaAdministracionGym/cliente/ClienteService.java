package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    public ClienteResponse create(ClienteRequest request) {

        if (clienteRepository.existsByCedula(request.getCedula())) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_CEDULA_ALREADY_EXISTS);
        }
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_EMAIL_ALREADY_EXISTS);
        }
        if (clienteRepository.existsByTelefono(request.getTelefono())) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_PHONE_ALREADY_EXISTS);
        }

        // (Opcional) reglas de fecha/edad si quieres controlarlas aquí también
        if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isBefore(LocalDate.now())) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_FECHA_NACIMIENTO_INVALIDA);
        }
        if (request.getFechaNacimiento() != null &&
                Period.between(request.getFechaNacimiento(), LocalDate.now()).getYears() < 12) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_UNDERAGE);
        }

        Cliente cliente = clienteMapper.toCliente(request);

        cliente.setCodigoInterno(null);
        cliente.setIsVisible(true);
        cliente = clienteRepository.save(cliente);

        if (cliente.getId() == null) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_CODIGO_INTERNO_ERROR);
        }

        int year = LocalDate.now().getYear();
        String correlativo = String.format("%06d", cliente.getId());
        cliente.setCodigoInterno("CLI-" + year + "-" + correlativo);

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
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CLIENTE_NOT_FOUND));
        return clienteMapper.toClienteResponse(cliente);
    }

    public ClienteResponse findByCedula(String cedula) {
        Cliente cliente = clienteRepository.findByCedula(cedula)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CLIENTE_NOT_FOUND));
        return clienteMapper.toClienteResponse(cliente);
    }

    @Transactional
    public ClienteResponse update(Long id, ClienteRequest request) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CLIENTE_NOT_FOUND));

        if (!cliente.getCedula().equals(request.getCedula())
                && clienteRepository.existsByCedula(request.getCedula())) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_CEDULA_ALREADY_EXISTS);
        }

        if (!cliente.getEmail().equalsIgnoreCase(request.getEmail())
                && clienteRepository.existsByEmail(request.getEmail())) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_EMAIL_ALREADY_EXISTS);
        }

        if (!cliente.getTelefono().equals(request.getTelefono())
                && clienteRepository.existsByTelefono(request.getTelefono())) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_PHONE_ALREADY_EXISTS);
        }

        if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isBefore(LocalDate.now())) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_FECHA_NACIMIENTO_INVALIDA);
        }

        clienteMapper.updateClienteFromRequest(cliente, request);

        // 🔥 OJO: te faltaba guardar (si tu JPA no está gestionando cambios como esperas)
        cliente = clienteRepository.save(cliente);

        return clienteMapper.toClienteResponse(cliente);
    }



    public void delete(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.CLIENTE_NOT_FOUND));

        try {
            clienteRepository.delete(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new BussinessException(BusinessErrorCodes.CLIENTE_DELETE_NOT_ALLOWED);
        }
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
