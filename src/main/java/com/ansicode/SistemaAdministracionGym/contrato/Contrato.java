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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Contrato extends AuditedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private String archivoUrl;

    @Enumerated(EnumType.STRING)
    private EstadoContrato estadoContrato;
}
