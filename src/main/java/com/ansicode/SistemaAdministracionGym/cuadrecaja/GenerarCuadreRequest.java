package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerarCuadreRequest {

    private String moneda = "USD";
    private String observacion;
}
