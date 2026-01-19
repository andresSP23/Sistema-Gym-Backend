package com.ansicode.SistemaAdministracionGym.sesioncaja;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "caja_sesiones",
        indexes = {
                @Index(name = "ix_caja_sesiones_estado_sucursal", columnList = "estado, sucursal_id"),
                @Index(name = "ix_caja_sesiones_apertura", columnList = "fecha_apertura"),
                @Index(name = "ix_caja_sesiones_cierre", columnList = "fecha_cierre")
        })
public class SesionCaja extends AuditedEntity {

    @Column(name = "sucursal_id", nullable = false)
    private Long sucursalId;

    @Column(name = "usuario_apertura_id", nullable = false)
    private Long usuarioAperturaId;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura = LocalDateTime.now();

    @Column(name = "base_inicial_efectivo", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseInicialEfectivo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSesionCaja estado = EstadoSesionCaja.ABIERTA;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "usuario_cierre_id")
    private Long usuarioCierreId;

    @Column(length = 300)
    private String observacion;
}