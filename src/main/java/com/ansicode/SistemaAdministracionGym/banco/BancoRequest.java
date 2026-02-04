package com.ansicode.SistemaAdministracionGym.banco;

import com.ansicode.SistemaAdministracionGym.enums.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BancoRequest {
    @NotBlank
    private String nombre;
    @NotBlank
    private String numeroCuenta;
    @NotNull
    private TipoCuenta tipoCuenta;
    private BigDecimal saldoInicial;
    @NotBlank
    private String titular;
}
    