package com.ansicode.SistemaAdministracionGym.comprobante;

import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.enums.EstadoComprobante;
import com.ansicode.SistemaAdministracionGym.enums.TipoComprobante;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comprobantes",
        indexes = {
                @Index(name = "ix_comprobantes_numero", columnList = "numero"),
                @Index(name = "ix_comprobantes_tipo", columnList = "tipo"),
                @Index(name = "ix_comprobantes_estado", columnList = "estado")
        })
public class Comprobante extends AuditedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Column(nullable = false, length = 40)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoComprobante tipo; // FACTURA/RECIBO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoComprobante estado = EstadoComprobante.GENERADO;

    @Column(name = "pdf_ref", length = 300)
    private String pdfRef;
}