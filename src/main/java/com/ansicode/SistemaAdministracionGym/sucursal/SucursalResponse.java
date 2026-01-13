package com.ansicode.SistemaAdministracionGym.sucursal;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class SucursalResponse {

    private Long id;

    private String nombre;
    private String codigoSucursal;

    private String direccion;
    private String ciudad;
    private String provincia;
    private String pais;

    private String telefono;
    private String email;

    private LocalTime horaApertura;
    private LocalTime horaCierre;

    private LocalDate fechaApertura;
    private Integer aforoMaximo;

    private String ruc;
    private String razonSocial;

    private String logoUrl;
    private String colorPrimario;

    private Boolean activo;
}
