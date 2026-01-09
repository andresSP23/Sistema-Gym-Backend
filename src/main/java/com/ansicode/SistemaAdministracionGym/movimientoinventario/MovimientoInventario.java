package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
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
@Table(name = "movimientos_inventario")
@Getter
@Setter
@SQLDelete(sql = "UPDATE movimientos_inventario SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovimientoInventario extends BaseEntity {

    @ManyToOne(optional = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimientoInventario tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer stockAnterior;

    @Column(nullable = false)
    private Integer stockActual;

    @Column(nullable = false)
    private LocalDateTime fechaMovimiento;

    @ManyToOne(optional = false)
    private User usuario;
}
