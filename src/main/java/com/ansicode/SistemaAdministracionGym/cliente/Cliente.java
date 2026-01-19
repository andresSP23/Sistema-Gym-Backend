package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoMembresia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@SQLDelete(sql = "UPDATE Cliente SET is_visible = false WHERE id = ?")
@Where(clause = "is_visible = true")
@AllArgsConstructor
@NoArgsConstructor
public class Cliente  extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(nullable = false, length = 50)
    private String nombres;

    @Column(nullable = false, length = 50)
    private String apellidos;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 20 ,unique = true)
    private String telefono;
    @Column(nullable = false, length = 255)
    private String direccion;
    @Column(nullable = false)
    private Integer edad;

    @Column(unique = true)
    private String codigoInterno;

    private LocalDate fechaRegistro;

    @Enumerated(EnumType.STRING)
    private EstadoMembresia estado;



    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
