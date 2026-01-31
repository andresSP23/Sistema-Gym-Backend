package com.ansicode.SistemaAdministracionGym.conteocaja;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
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
@Table(name = "caja_conteos", indexes = {
                @Index(name = "ix_caja_conteos_sesion", columnList = "sesion_caja_id"),
                @Index(name = "ix_caja_conteos_sesion_moneda", columnList = "sesion_caja_id, moneda")
})
public class ConteoCaja extends AuditedEntity {

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "sesion_caja_id", nullable = false)
        private SesionCaja sesionCaja;

        @Column(nullable = false, length = 10)
        private String moneda = "USD";

        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal denominacion;

        @Column(nullable = false)
        private Integer cantidad;

        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal subtotal;
}