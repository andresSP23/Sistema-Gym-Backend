package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import com.ansicode.SistemaAdministracionGym.banco.Banco;
import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_banco")
@SQLDelete(sql = "UPDATE movimientos_banco SET is_visible = false WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("is_visible = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovimientoBanco extends AuditedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "banco_id")
    private Banco banco;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco concepto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimientoBanco tipo; // INGRESO, EGRESO

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private com.ansicode.SistemaAdministracionGym.enums.OrigenMovimientoBanco origen;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 500)
    private String descripcion;

    // Referencia estandarizada (e.g., "PAGO#P-55 | CLIENTE#C-10 | Membresía
    // Mensual")
    @Column(length = 300)
    private String referencia;

    // We could link specifically to entities like Equipamiento ID later if needed
    // using generic ID or specific join,
    // but a string reference is flexible for now.
}
