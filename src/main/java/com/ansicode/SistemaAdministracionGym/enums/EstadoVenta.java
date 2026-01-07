package com.ansicode.SistemaAdministracionGym.enums;

public enum EstadoVenta {
    PENDIENTE,     // Venta creada, aún no pagada
    PAGADA,        // Pago confirmado
    PARCIAL,       // Pago parcial (deudas)
    CANCELADA,     // Anulada (no afecta stock si no se confirmó)
    DEVUELTA
}
