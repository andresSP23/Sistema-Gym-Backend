package com.ansicode.SistemaAdministracionGym.gasto;

import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagarGastoRequest {
    @NotNull
    private MetodoPago metodoPago;
    private Long bancoId;
}
