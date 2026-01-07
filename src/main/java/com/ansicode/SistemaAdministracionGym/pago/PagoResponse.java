package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagoResponse {
    private Long id;

    private BigDecimal monto;
    private MetodoPago metodoPago;
    private LocalDateTime fechaPago;
    private EstadoPago estadoPago;

    private Long ventaId;
    private String ventaCodigo; // opcional para mostrar info legible

    private Long membresiaClienteId;
    private String clienteNombre; // opcional para mostrar info legible

    private boolean activo;
}
