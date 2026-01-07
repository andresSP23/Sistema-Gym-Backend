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

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovimientoInventario extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    private TipoMovimientoInventario tipoMovimiento;

    private LocalDateTime fechaMovimiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private User usuario;
}
