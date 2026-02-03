package com.ansicode.SistemaAdministracionGym.contrato.plantilla;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plantillas_contratos")
public class PlantillaContrato extends AuditedEntity {

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido; // HTML or Markdown content with placeholders {{CLIENTE}}, {{FECHA}}, etc.

    @Column(nullable = false)
    private boolean activo;
}
