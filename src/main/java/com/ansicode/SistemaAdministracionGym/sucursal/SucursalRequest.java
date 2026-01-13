package com.ansicode.SistemaAdministracionGym.sucursal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class SucursalRequest {


    @NotBlank
    private String nombre;

    @NotBlank
    private String codigoSucursal;

    @NotBlank
    private String direccion;

    @NotBlank
    private String ciudad;

    @NotBlank
    private String provincia;

    @NotBlank
    private String pais;

    @NotBlank
    private String telefono;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private LocalTime horaApertura;

    @NotNull
    private LocalTime horaCierre;

    @NotNull
    private LocalDate fechaApertura;

    @NotNull
    @Min(1)
    private Integer aforoMaximo;

    @NotBlank
    private String ruc;

    @NotBlank
    private String razonSocial;

    private String logoUrl;
    private String colorPrimario;
}
