package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoEquipamiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "equipamientos")
@SQLDelete(sql = "UPDATE equipamientos SET is_visible = false WHERE id = ?")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Equipamiento extends AuditedEntity {
    private String nombre;
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    private EstadoEquipamiento estadoEquipamiento;

    private String fotoUrl;

}
