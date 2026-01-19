package com.ansicode.SistemaAdministracionGym.movimientodinero;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import com.ansicode.SistemaAdministracionGym.servicio.Servicios;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
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
@Table(name = "movimientos_dinero",
        indexes = {
                @Index(name = "ix_mov_dinero_sesion_fecha", columnList = "sesion_caja_id, fecha"),
                @Index(name = "ix_mov_dinero_tipo", columnList = "tipo"),
                @Index(name = "ix_mov_dinero_concepto", columnList = "concepto"),
                @Index(name = "ix_mov_dinero_metodo", columnList = "metodo"),
                @Index(name = "ix_mov_dinero_pago", columnList = "pago_id"),
                @Index(name = "ix_mov_dinero_venta", columnList = "venta_id")
        })
public class MovimientoDinero extends AuditedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sesion_caja_id", nullable = false)
    private SesionCaja sesionCaja;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimientoDinero tipo; // INGRESO/EGRESO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConceptoMovimientoDinero concepto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodo;

    @Column(nullable = false, length = 10)
    private String moneda = "USD";

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(length = 250)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    // Trazabilidad (opcionales)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id")
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id")
    private Servicios servicio;

    // Para enlazar compra stock sin depender de tu entidad de Producto
    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
}