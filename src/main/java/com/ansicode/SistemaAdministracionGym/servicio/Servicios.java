package com.ansicode.SistemaAdministracionGym.servicio;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
@Table(name = "servicios", indexes = {
                @Index(name = "ix_servicios_activo", columnList = "estado"),
                @Index(name = "ix_servicios_suscripcion", columnList = "es_suscripcion")
})
@SQLDelete(sql = "UPDATE servicios SET is_visible = false WHERE id = ?")
@Where(clause = "is_visible = true")
public class Servicios extends AuditedEntity {

        @Column(nullable = false, length = 120)
        private String nombre;

        @Column(length = 255)
        private String descripcion;

        @Column(name = "es_suscripcion", nullable = false)
        private boolean esSuscripcion;

        @Column(name = "duracion_dias")
        private Integer duracionDias;

        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal precio;

        @Column(nullable = false)
        private boolean estado = true;

}
