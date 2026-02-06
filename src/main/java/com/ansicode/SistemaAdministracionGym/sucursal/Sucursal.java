package com.ansicode.SistemaAdministracionGym.sucursal;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sucursal")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE sucursal SET is_visible = false WHERE id = ?")
@Where(clause = "is_visible = true")
@AllArgsConstructor
@SuperBuilder

public class Sucursal extends AuditedEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 20)
    private String codigoSucursal;

    @Column(nullable = false)
    @lombok.Builder.Default
    private Long ultimoNumeroFactura = 0L;

    // Ubicación
    private String direccion;
    private String ciudad;
    private String provincia;
    private String pais;

    // Contacto
    private String telefono;
    private String email;

    // Horarios
    private LocalTime horaApertura;
    private LocalTime horaCierre;

    // Administración
    private LocalDate fechaApertura;
    private Integer aforoMaximo;

    // Fiscal
    private String ruc;
    private String razonSocial;

    // Branding
    private String logoUrl;
    private String colorPrimario;

}
