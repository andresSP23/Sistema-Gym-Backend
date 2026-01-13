package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProducto;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.enums.TipoProducto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@SQLDelete(sql = "UPDATE productos SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public class Producto extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_producto_id")
    private CategoriaProducto categoriaProducto;

    // Ganancia calculada (no persistente)
    @Transient
    public BigDecimal getGanancia() {
        return precioVenta.subtract(precioCompra);
    }

}
