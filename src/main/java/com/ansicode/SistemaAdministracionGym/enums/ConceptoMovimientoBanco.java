package com.ansicode.SistemaAdministracionGym.enums;

/**
 * Concepto específico para movimientos bancarios.
 * Define la clasificación del movimiento para reportes y trazabilidad.
 */
public enum ConceptoMovimientoBanco {
    // Ingresos
    VENTA_PRODUCTO,
    VENTA_SERVICIO,
    PAGO_SUSCRIPCION,
    DEPOSITO,

    // Egresos - Compras
    COMPRA_STOCK,
    COMPRA_EQUIPAMIENTO,

    // Egresos - Servicios Básicos
    PAGO_LUZ,
    PAGO_AGUA,
    PAGO_INTERNET,
    PAGO_ALQUILER,

    // Egresos - Operativos
    PAGO_NOMINA,
    IMPUESTOS,
    MANTENIMIENTO,

    // Devoluciones
    DEVOLUCION_CANCELACION_SUSCRIPCION,
    DEVOLUCION_VENTA,

    // Otros
    AJUSTE,
    OTROS
}
