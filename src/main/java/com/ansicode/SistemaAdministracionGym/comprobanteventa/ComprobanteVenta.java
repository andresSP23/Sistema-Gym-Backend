package com.ansicode.SistemaAdministracionGym.comprobanteventa;

import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "comprobante_ventas")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE comprobante_ventas SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@AllArgsConstructor
@SuperBuilder

public class ComprobanteVenta extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido; // JSON del comprobante

    @Lob
    @Column(name = "pdf_data") // BLOB para el PDF
    private byte[] pdfData;

    @Column(nullable = false)
    private LocalDateTime fechaGeneracion;
}
