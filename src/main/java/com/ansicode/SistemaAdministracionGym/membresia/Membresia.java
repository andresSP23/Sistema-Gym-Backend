package com.ansicode.SistemaAdministracionGym.membresia;

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

import java.math.BigDecimal;

@Entity
@Table(name = "membresias")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE membresias SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@AllArgsConstructor
@SuperBuilder
public class Membresia  extends BaseEntity {

    @Column(nullable = false, length = 50 , unique = true)
    private String nombre;

    @Column(nullable = false)
    private Integer duracionDias;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false , length = 255)
    private String descripcion;

    private Boolean permiteCongelacion = false;
}
