package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembresiaClienteResponse {
    private Long id;

    private Long clienteId;
    private String clienteNombre; // nombres + apellidos del cliente

    private Long membresiaId;
    private String membresiaNombre;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private EstadoMembresia estado;

    private boolean activo;
}
