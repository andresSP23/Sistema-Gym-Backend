package com.ansicode.SistemaAdministracionGym.asistencia;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "asistencias",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cliente_id", "fechaEntrada"})
)
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE asistencias SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@AllArgsConstructor
@SuperBuilder
public class Asistencia extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDateTime fechaEntrada;
}
