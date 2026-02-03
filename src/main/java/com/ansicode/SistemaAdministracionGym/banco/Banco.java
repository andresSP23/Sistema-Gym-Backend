package com.ansicode.SistemaAdministracionGym.banco;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.enums.TipoCuenta;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import java.math.BigDecimal;

@Entity
@Table(name = "bancos")
@SQLDelete(sql = "UPDATE bancos SET is_visible = false WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("is_visible = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Banco extends AuditedEntity {

    @Column(nullable = false)
    private String nombre; // e.g., "Banco Pichincha"

    @Column(nullable = false)
    private String numeroCuenta;

    @Column(length = 100)
    private String titular;

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
