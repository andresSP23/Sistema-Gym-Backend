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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "membresia_cliente_id")
    private MembresiaCliente membresiaCliente;
}
