package com.ansicode.SistemaAdministracionGym.asistencia;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private LocalDateTime fechaEntrada;

    private boolean membresiaActiva;
    private boolean pagosPendientes;

    private Long membresiaClienteId; // Opcional: info extra
    private String membresiaNombre;
}
