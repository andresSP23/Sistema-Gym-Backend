package com.ansicode.SistemaAdministracionGym.banco;

import org.springframework.stereotype.Service;

@Service
public class BancoMapper {

    public Banco toEntity(BancoRequest request) {
        return Banco.builder()
                .nombre(request.getNombre())
                .titular(request.getTitular())
                .numeroCuenta(request.getNumeroCuenta())
                .tipoCuenta(request.getTipoCuenta())
                .saldo(request.getSaldoInicial() != null ? request.getSaldoInicial() : java.math.BigDecimal.ZERO)
                .activo(true)
                .build();
    }

    public BancoResponse toResponse(Banco entity) {
        return BancoResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .titular(entity.getTitular())
                .numeroCuenta(entity.getNumeroCuenta())
                .tipoCuenta(entity.getTipoCuenta())
                .saldo(entity.getSaldo())
                .activo(entity.isActivo())
                .build();
    }

    public com.ansicode.SistemaAdministracionGym.banco.movimiento.MovimientoBancoResponse toMovimientoResponse(
            com.ansicode.SistemaAdministracionGym.banco.movimiento.MovimientoBanco entity) {
        return com.ansicode.SistemaAdministracionGym.banco.movimiento.MovimientoBancoResponse.builder()
                .id(entity.getId())
                .tipo(entity.getTipo())
                .monto(entity.getMonto())
                .fecha(entity.getFecha())
                .descripcion(entity.getDescripcion())
                .referencia(entity.getReferencia())
                .build();
    }
}
