package com.ansicode.SistemaAdministracionGym.conteocaja;


import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConteoCajaResponse {

    private Long sesionCajaId;
    private String moneda;

    private List<ConteoCajaItemResponse> items;

    private BigDecimal totalContado;
}
