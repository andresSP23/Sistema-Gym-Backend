package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoContrato;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "contratos")
@SQLDelete(sql = "UPDATE contratos SET is_visible = false WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("is_visible = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Contrato extends AuditedEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToOne
    @JoinColumn(name = "cliente_suscripcion_id")
    private com.ansicode.SistemaAdministracionGym.clientesuscripcion.ClienteSuscripcion suscripcion;

    @Column(columnDefinition = "TEXT")
    private String contenidoContrato; // Snapshot of the text at the moment of creation

    private String archivoUrl;

    @Enumerated(EnumType.STRING)
    private EstadoContrato estadoContrato;

    private java.time.LocalDateTime fechaFirma;
}
