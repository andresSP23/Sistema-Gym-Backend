package com.ansicode.SistemaAdministracionGym.pago.event;

import com.ansicode.SistemaAdministracionGym.pago.Pago;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PagoCompletadoEvent {
    private final Pago pago;
}
