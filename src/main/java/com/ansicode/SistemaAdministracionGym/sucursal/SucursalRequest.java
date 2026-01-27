package com.ansicode.SistemaAdministracionGym.sucursal;

import com.ansicode.SistemaAdministracionGym.validation.horarioValido.HorarioValido;
import com.ansicode.SistemaAdministracionGym.validation.ruc.RucEcuatoriano;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@HorarioValido
public class SucursalRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El código de la sucursal es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigoSucursal;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;

    @NotBlank(message = "El país es obligatorio")
    private String pais;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^[0-9+ ]+$",
            message = "El teléfono solo puede contener números"
    )
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    @NotNull(message = "La hora de apertura es obligatoria")
    private LocalTime horaApertura;

    @NotNull(message = "La hora de cierre es obligatoria")
    private LocalTime horaCierre;

    @NotNull(message = "El aforo máximo es obligatorio")
    @Min(value = 1, message = "El aforo máximo debe ser mayor a 0")
    private Integer aforoMaximo;

    @NotBlank(message = "El RUC es obligatorio")
    private String ruc;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 150, message = "La razón social no puede exceder 150 caracteres")
    private String razonSocial;

    private String logoUrl;
    private String colorPrimario;
}
