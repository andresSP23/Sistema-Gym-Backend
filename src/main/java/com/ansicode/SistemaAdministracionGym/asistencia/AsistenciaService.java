package com.ansicode.SistemaAdministracionGym.asistencia;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final ClienteRepository clienteRepository;
    private final AsistenciaMapper asistenciaMapper;

    @Transactional
    public AsistenciaResponse create(AsistenciaRequest request) {
        // Buscar cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId().longValue())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        // Evitar duplicados en el mismo día (opcional)
        LocalDateTime inicioDia = request.getFechaEntrada().toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1).minusSeconds(1);

        if (asistenciaRepository.existsByClienteIdAndFechaEntradaBetween(
                cliente.getId(),
                inicioDia,
                finDia
        )) {
            throw new IllegalArgumentException("El cliente ya tiene asistencia registrada para este día");
        }

        // Crear entidad y guardar
        Asistencia asistencia = asistenciaMapper.toAsistencia(request, cliente);
        asistencia.setActivo(true);

        asistenciaRepository.save(asistencia);

        return asistenciaMapper.toAsistenciaResponse(asistencia);
    }

    public Page<AsistenciaResponse> findByCliente(Long clienteId, Pageable pageable) {
        Page<Asistencia> page = asistenciaRepository.findByClienteId(clienteId, pageable);
        return page.map(asistenciaMapper::toAsistenciaResponse);
    }

    @Transactional
    public void delete(Long id) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asistencia no encontrada"));
        asistenciaRepository.delete(asistencia);
    }

    public AsistenciaResponse findById(Long id) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asistencia no encontrada"));
        return asistenciaMapper.toAsistenciaResponse(asistencia);
    }
}
