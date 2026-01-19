package com.ansicode.SistemaAdministracionGym.detalleventa;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
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


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venta_detalles",
        indexes = {
                @Index(name = "ix_venta_detalles_venta", columnList = "venta_id"),
                @Index(name = "ix_venta_detalles_tipo", columnList = "tipo_item"),
                @Index(name = "ix_venta_detalles_ref", columnList = "referencia_id")
        })
public class DetalleVenta extends AuditedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 20)
    private TipoItemVenta tipoItem; // PRODUCTO o SERVICIO

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    // Snapshot para factura / historial
    @Column(name = "descripcion_snapshot", nullable = false, length = 200)
    private String descripcionSnapshot;

    @Column(name = "precio_unitario_snapshot", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitarioSnapshot;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;

    @Column(name = "total_linea", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalLinea;
}
