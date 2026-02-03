package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoInventario;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.user.User;
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
@Table(name = "movimientos_inventario", indexes = {
                @Index(name = "ix_mov_inv_producto_fecha", columnList = "producto_id, created_at"),
                @Index(name = "ix_mov_inv_tipo", columnList = "tipo_movimiento")
})
@SQLDelete(sql = "UPDATE movimientos_inventario SET is_visible = false WHERE id = ?")
@Where(clause = "is_visible = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovimientoInventario extends AuditedEntity {

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "producto_id", nullable = false)
        private Producto producto;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_movimiento", nullable = false, length = 20)
        private TipoMovimientoInventario tipoMovimiento;

        @Column(nullable = false)
        private Integer cantidad;

        @Column(length = 255)
        private String observacion;

        @Column(name = "stock_anterior", nullable = false)
        private Integer stockAnterior;

        @Column(name = "stock_actual", nullable = false)
        private Integer stockActual;

}