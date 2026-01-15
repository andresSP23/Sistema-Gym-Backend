package com.ansicode.SistemaAdministracionGym.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AsistenciaUltimaDto    {

    private String nombreCliente;
    private LocalDateTime fechaEntrada;
}
