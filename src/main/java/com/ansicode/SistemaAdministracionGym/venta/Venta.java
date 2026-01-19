package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.sucursal.Sucursal;
import com.ansicode.SistemaAdministracionGym.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ventas",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_ventas_sucursal_factura", columnNames = {"sucursal_id", "numero_factura"})
        },
        indexes = {
                @Index(name = "ix_ventas_fecha", columnList = "fecha_venta"),
                @Index(name = "ix_ventas_factura", columnList = "numero_factura"),
                @Index(name = "ix_ventas_cliente_fecha", columnList = "cliente_id, fecha_venta"),
                @Index(name = "ix_ventas_estado", columnList = "estado")
        })
public class Venta extends AuditedEntity {

    @Column(name = "numero_factura", nullable = false, length = 30)
    private String numeroFactura;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cajero_usuario_id")
    private User cajeroUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVenta estado = EstadoVenta.BORRADOR;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "descuento_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuentoTotal = BigDecimal.ZERO;

    @Column(name = "impuesto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestoTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleVenta d) {
        d.setVenta(this);
        detalles.add(d);
    }

    public void quitarDetalle(DetalleVenta d) {
        detalles.remove(d);
        d.setVenta(null);
    }
}