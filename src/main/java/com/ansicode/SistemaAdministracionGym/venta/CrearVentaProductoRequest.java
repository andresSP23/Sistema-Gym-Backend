package com.ansicode.SistemaAdministracionGym.venta;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrearVentaProductoRequest {
    @NotNull
    private Long sucursalId;

    // opcional (mostrador)
    private Long clienteId;

    @NotEmpty
    private List<ItemProductoRequest> items;
}
