package com.ansicode.SistemaAdministracionGym.enums;

/**
 * Origen del movimiento bancario para trazabilidad.
 * Indica cómo se generó el movimiento.
 */
public enum OrigenMovimientoBanco {
    MANUAL, // Creado manualmente por usuario
    PAGO, // Generado automáticamente por un pago
    SUSCRIPCION, // Generado por operación de suscripción
    GASTO, // Generado por registro de gasto
    AJUSTE // Ajuste contable
}
