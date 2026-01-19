package com.ansicode.SistemaAdministracionGym.sesioncaja;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AbrirCajaRequest {
    @NotNull(message = "sucursalId es obligatorio")
    private Long sucursalId;

    @NotNull(message = "baseInicialEfectivo es obligatorio")
    @DecimalMin(value = "0.00", message = "baseInicialEfectivo no puede ser negativo")
    private BigDecimal baseInicialEfectivo;

    @Size(max = 300, message = "observacion no puede exceder 300 caracteres")
    private String observacion;
}
