package com.ansicode.SistemaAdministracionGym.membresiacliente;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import com.ansicode.SistemaAdministracionGym.membresia.Membresia;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE usuarios SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@SuperBuilder
@Table(name = "membresias_clientes")

public class MembresiaCliente extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "membresia_id")
    private Membresia membresia;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    private EstadoMembresia estado;
}
