package com.ansicode.SistemaAdministracionGym.banco;

import com.ansicode.SistemaAdministracionGym.enums.TipoCuenta;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BancoResponse {
    private Long id;
    private String nombre;
    private String titular;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldo;
    private boolean activo;
}
