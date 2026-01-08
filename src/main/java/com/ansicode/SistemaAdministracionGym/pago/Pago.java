package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.membresiacliente.MembresiaCliente;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE pagos SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@Table(name = "pagos")
@SuperBuilder


public class Pago extends BaseEntity {
    @Column(nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago;



    @ManyToOne
    @JoinColumn(name = "membresia_cliente_id")
    private MembresiaCliente membresiaCliente;
}
