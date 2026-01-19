package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.comprobante.Comprobante;
import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoComprobante;
import com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago;
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

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos",
        indexes = {
                @Index(name = "ix_pagos_fecha", columnList = "fecha_pago"),
                @Index(name = "ix_pagos_tipo_operacion", columnList = "tipo_operacion"),
                @Index(name = "ix_pagos_metodo_estado", columnList = "metodo, estado"),
                @Index(name = "ix_pagos_cliente_fecha", columnList = "cliente_id, fecha_pago"),
                @Index(name = "ix_pagos_venta", columnList = "venta_id")
        })
public class Pago extends AuditedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodo;

    @Column(nullable = false, length = 10)
    private String moneda = "USD";

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "efectivo_recibido", precision = 12, scale = 2)
    private BigDecimal efectivoRecibido; // solo EFECTIVO

    @Column(precision = 12, scale = 2)
    private BigDecimal cambio; // solo EFECTIVO

    @Column(name = "referencia_transaccion", length = 80)
    private String referenciaTransaccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacion", nullable = false, length = 20)
    private TipoOperacionPago tipoOperacion; // PRODUCTO/SERVICIO/MIXTO

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    private TipoComprobante tipoComprobante;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id")
    private Comprobante comprobante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;
}