package com.ansicode.SistemaAdministracionGym.clase;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "clases")
@SQLDelete(sql = "UPDATE clases SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Clase extends AuditedEntity {

    private String nombre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entrenador_id")
    private User entrenador;

}
