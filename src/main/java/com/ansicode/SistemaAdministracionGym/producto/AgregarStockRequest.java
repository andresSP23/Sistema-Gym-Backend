package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AgregarStockRequest {

    private Long sucursalId;
    private Integer cantidad;
    private String observacion;

    private Boolean registrarEgreso = false; // <- ESTE

    private MetodoPago metodoPago;
    private String moneda = "USD";
    private Long bancoId;
}
