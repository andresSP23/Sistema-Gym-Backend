package com.ansicode.SistemaAdministracionGym.mantenimiento;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.equipamiento.Equipamiento;
import com.ansicode.SistemaAdministracionGym.enums.TipoMantenimiento;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "mantenimientos")
@SQLDelete(sql = "UPDATE mantenimientos SET is_visible = false WHERE id = ?")
// @Where(clause = "is_visible = true") // Hibernate 6.3+ uses @SQLRestriction
@org.hibernate.annotations.SQLRestriction("is_visible = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Mantenimiento extends AuditedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipamiento_id")
    private Equipamiento equipamiento;

    private LocalDateTime fechaRealizacion;

    @Enumerated(EnumType.STRING)
    private TipoMantenimiento tipo;

    private BigDecimal costo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String tecnico; // Puede ser nombre de empresa o persona

    private LocalDate proximoMantenimientoSugerido;
}
