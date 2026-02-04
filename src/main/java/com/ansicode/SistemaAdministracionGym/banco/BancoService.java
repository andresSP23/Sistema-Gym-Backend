package com.ansicode.SistemaAdministracionGym.banco;

import com.ansicode.SistemaAdministracionGym.banco.movimiento.MovimientoBanco;
import com.ansicode.SistemaAdministracionGym.banco.movimiento.MovimientoBancoRepository;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BancoService {

    private final BancoRepository bancoRepository;
    private final BancoMapper bancoMapper;
    private final MovimientoBancoRepository movimientoBancoRepository;

    @Transactional
    public BancoResponse create(BancoRequest request) {
        Banco banco = bancoMapper.toEntity(request);
        return bancoMapper.toResponse(bancoRepository.save(banco));
    }

    public List<BancoResponse> findAll() {
        return bancoRepository.findAll().stream()
                .map(bancoMapper::toResponse)
                .toList();
    }

    @Transactional
    public void registrarMovimiento(Long bancoId, TipoMovimientoBanco tipo, BigDecimal monto, String descripcion,
            String referencia, com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco concepto,
            com.ansicode.SistemaAdministracionGym.enums.OrigenMovimientoBanco origen) {
        Banco banco = bancoRepository.findById(bancoId)
                .orElseThrow(() -> new RuntimeException("Banco no encontrado"));

        if (tipo == TipoMovimientoBanco.INGRESO) {
            banco.setSaldo(banco.getSaldo().add(monto));
        } else {
            banco.setSaldo(banco.getSaldo().subtract(monto));
        }
        bancoRepository.save(banco);

        MovimientoBanco mov = MovimientoBanco.builder()
                .banco(banco)
                .tipo(tipo)
                .concepto(concepto)
                .origen(origen)
                .monto(monto)
                .fecha(LocalDateTime.now())
                .descripcion(descripcion)
                .referencia(referencia)
                .build();
        movimientoBancoRepository.save(mov);
    }

    public List<com.ansicode.SistemaAdministracionGym.banco.movimiento.MovimientoBancoResponse> getMovimientos(
            Long bancoId) {
        return movimientoBancoRepository.findByBancoId(bancoId).stream()
                .map(bancoMapper::toMovimientoResponse)
                .toList();
    }

    @Transactional
    public BancoResponse update(Long id, BancoRequest request) {
        Banco banco = bancoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banco no encontrado"));

        banco.setNombre(request.getNombre());
        banco.setNumeroCuenta(request.getNumeroCuenta());
        banco.setTipoCuenta(request.getTipoCuenta());
        banco.setTitular(request.getTitular());

        return bancoMapper.toResponse(bancoRepository.save(banco));
    }

    @Transactional
    public void delete(Long id) {
        if (!bancoRepository.existsById(id)) {
            throw new RuntimeException("Banco no encontrado");
        }
        bancoRepository.deleteById(id);
    }
}
