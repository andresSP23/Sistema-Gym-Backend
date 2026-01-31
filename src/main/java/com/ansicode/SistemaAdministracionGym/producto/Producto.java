package com.ansicode.SistemaAdministracionGym.producto;

import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProducto;
import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
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
@SQLDelete(sql = "UPDATE productos SET is_visible = false WHERE id = ?")
@Where(clause = "is_visible = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public class Producto extends AuditedEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_producto_id", nullable = false)
    private CategoriaProducto categoriaProducto;

    // Ganancia calculada (no persistente)
    @Transient
    public BigDecimal getGanancia() {
        return precioVenta.subtract(precioCompra);
    }

}
