package com.ansicode.SistemaAdministracionGym.producto;

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

    private BigDecimal precio;

    private Integer stock;

    @Enumerated(EnumType.STRING)
    private TipoProducto tipoProducto;

}
