package com.ansicode.SistemaAdministracionGym.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventarioResumenDto {

    private String producto;
    private Long cantidad;


}
