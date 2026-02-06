package com.ansicode.SistemaAdministracionGym.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardAsistenciaResponse {
    private Long id;
    private String clienteNombre;
    private LocalDateTime fechaEntrada;
}
