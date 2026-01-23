package com.ansicode.SistemaAdministracionGym.sesioncaja;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class SesionCajaResponse {

    private Long id;
    private Long sucursalId;

    private Long usuarioAperturaId;
    private String usuarioAperturaNombre;

    private LocalDateTime fechaApertura;
    private BigDecimal baseInicialEfectivo;

    private EstadoSesionCaja estado;

    private LocalDateTime fechaCierre;

    private Long usuarioCierreId;
    private String usuarioCierreNombre;

    private String observacion;

    // saldo final viene del cuadre
    private BigDecimal saldoFinal;   // CuadreCaja.efectivoContado
    private BigDecimal diferencia;
}
