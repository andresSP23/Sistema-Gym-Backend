package com.ansicode.SistemaAdministracionGym.equipamiento;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoEquipamiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "equipamientos")
@SQLDelete(sql = "UPDATE equipamientos SET is_visible = false WHERE id = ?")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Equipamiento extends AuditedEntity {
    private String nombre;
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    private EstadoEquipamiento estadoEquipamiento;

    private String fotoUrl;

    // --- Asset Management Fields ---
    private String marca;
    private String modelo;
    private String numeroSerie;

    private java.time.LocalDate fechaCompra;
    private java.math.BigDecimal costo;
    private String proveedor;
    private java.time.LocalDate garantiaFin;

    // --- Maintenance Scheduling ---
    private Integer frecuenciaMantenimientoDias; // Cada cuantos dias requiere mant.
    private java.time.LocalDate proximoMantenimiento; // Calculado o manual

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private com.ansicode.SistemaAdministracionGym.sucursal.Sucursal sucursal;
}
