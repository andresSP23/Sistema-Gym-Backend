package com.ansicode.SistemaAdministracionGym.categoriaproducto;

import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "categoria_producto")
@SQLDelete(sql = "UPDATE categoria_producto SET is_visible = false WHERE id = ?")
@Where(clause = "is_visible = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CategoriaProducto  extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column( length = 255)
    private String descripcion;

}
