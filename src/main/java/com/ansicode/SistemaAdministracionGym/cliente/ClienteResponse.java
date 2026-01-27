package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponse {
    private Long id;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private String codigoInterno;
}
