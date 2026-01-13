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
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE membresias_clientes SET activo = false WHERE id = ?")
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


    @Transient
    private long diasRestantes;

    public long getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(long diasRestantes) {
        this.diasRestantes = diasRestantes;
    }


    public boolean puedeEditarAsignacion() {
        return estado == EstadoMembresia.PENDIENTE_PAGO;
    }

    public void activar() {
        this.fechaInicio = LocalDate.now();
        this.fechaFin = fechaInicio.plusDays(membresia.getDuracionDias());
        this.estado = EstadoMembresia.ACTIVA;
    }

    public void vencer() {
        this.estado = EstadoMembresia.VENCIDA;
    }
}
