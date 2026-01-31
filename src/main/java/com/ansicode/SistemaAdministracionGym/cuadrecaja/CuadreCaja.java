package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoCuadreCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "caja_cuadres", uniqueConstraints = {
                @UniqueConstraint(name = "uq_caja_cuadre_sesion", columnNames = { "sesion_caja_id" })
}, indexes = {
                @Index(name = "ix_caja_cuadre_estado", columnList = "estado")
})
public class CuadreCaja extends AuditedEntity {

        @OneToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "sesion_caja_id", nullable = false)
        private SesionCaja sesionCaja;

        @Column(name = "efectivo_esperado", nullable = false, precision = 12, scale = 2)
        private BigDecimal efectivoEsperado;

        @Column(name = "efectivo_contado", nullable = false, precision = 12, scale = 2)
        private BigDecimal efectivoContado;

        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal diferencia;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private EstadoCuadreCaja estado; // PARCIAL/COMPLETO

        @Column(length = 300)
        private String observacion;
}