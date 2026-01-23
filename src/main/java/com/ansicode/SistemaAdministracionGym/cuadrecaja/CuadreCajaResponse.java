package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuadreCajaResponse {

    private Long id;

    // Relación
    private Long sesionCajaId;

    // Montos
    private BigDecimal efectivoEsperado;
    private BigDecimal efectivoContado;
    private BigDecimal diferencia;

    // Estado del cuadre: PARCIAL / COMPLETO
    private String estado;

    // Observación
    private String observacion;

  ;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    private Long createdBy;


    private Long updatedBy;

}
