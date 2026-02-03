package com.ansicode.SistemaAdministracionGym.gasto;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.enums.CategoriaGasto;
import com.ansicode.SistemaAdministracionGym.enums.EstadoGasto;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gastos")
@SQLDelete(sql = "UPDATE gastos SET is_visible = false WHERE id = ?")
public class Gasto extends AuditedEntity {

    @Column(nullable = false)
    private String nombre; // e.g: "Pago Luz Marzo"

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaGasto categoria;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDate fechaGasto; // Fecha de emisión de la factura/recibo

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGasto estado;

    // --- Datos del Pago (Solo si está PAGADO) ---
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @Column(name = "sucursal_id", nullable = false)
    private Long sucursalId;
}
