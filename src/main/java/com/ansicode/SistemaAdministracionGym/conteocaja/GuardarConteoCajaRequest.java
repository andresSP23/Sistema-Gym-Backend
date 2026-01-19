package com.ansicode.SistemaAdministracionGym.conteocaja;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GuardarConteoCajaRequest {

    @NotNull(message = "sesionCajaId es obligatorio")
    private Long sesionCajaId;

    @NotNull(message = "items es obligatorio")
    private List<@Valid ConteoCajaItemRequest> items;


}
