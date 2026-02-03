package com.ansicode.SistemaAdministracionGym.cliente;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@SQLDelete(sql = "UPDATE Cliente SET is_visible = false WHERE id = ?")
@SQLDelete(sql = "UPDATE Cliente SET is_visible = false WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("is_visible = true")
@AllArgsConstructor
@NoArgsConstructor
public class Cliente extends AuditedEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(nullable = false, length = 50)
    private String nombres;

    @Column(nullable = false, length = 50)
    private String apellidos;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 20, unique = true)
    private String telefono;
    @Column(nullable = false, length = 255)
    private String direccion;
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(unique = true)
    private String codigoInterno;

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
