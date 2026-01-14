package com.ansicode.SistemaAdministracionGym.comprobantepago;

import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ComprobantePagoResponse {
    private Long id;

    // Relación
    private Long pagoId;

    // Información del comprobante
    private String contenido;          // JSON del comprobante
    private LocalDateTime fechaGeneracion;

    // Estado / control
    private boolean activo;

    // Información legible (opcional, pero muy útil para frontend)
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;

    private Long membresiaClienteId;
    private String clienteNombre;
}
