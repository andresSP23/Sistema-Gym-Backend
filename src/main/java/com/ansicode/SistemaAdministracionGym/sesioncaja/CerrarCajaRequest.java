package com.ansicode.SistemaAdministracionGym.sesioncaja;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CerrarCajaRequest {

    @Size(max = 300, message = "observacion no puede exceder 300 caracteres")
    private String observacion;
}
