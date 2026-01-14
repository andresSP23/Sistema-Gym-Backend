package com.ansicode.SistemaAdministracionGym.comprobantepago;

import com.ansicode.SistemaAdministracionGym.common.BaseEntity;
import com.ansicode.SistemaAdministracionGym.pago.Pago;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "comprobante_pagos")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE comprobante_pagos SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
@AllArgsConstructor
@SuperBuilder


public class ComprobantePago extends BaseEntity {


    @ManyToOne(optional = false)
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido; // JSON

    @Lob
    @Column(name = "pdf_data")
    private byte[] pdfData;

    @Column(nullable = false)
    private LocalDateTime fechaGeneracion;
}
