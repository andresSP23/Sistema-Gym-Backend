# Frontend Implementation Prompt: Módulo de Gastos y Actualización de Equipamiento

Hola, hemos implementado una **nueva funcionalidad de Gastos (Tracker)** y **actualizado la lógica de pagos en Equipamiento**. Necesito que actualices el frontend (Angular) con las siguientes especificaciones:

---

## 1. Nuevo Módulo: Gestión de Gastos
Este módulo permitirá registrar facturas de servicios (Luz, Agua, Alquiler) y otros gastos operativos.

### 1.1 Interfaces (TypeScript)
Crea/Actualiza los archivos de modelos:

```typescript
// enums/categoria-gasto.enum.ts
export enum CategoriaGasto {
    SERVICIOS_BASICOS = 'SERVICIOS_BASICOS', // Luz, Agua
    ALQUILER = 'ALQUILER',
    MANTENIMIENTO_LOCAL = 'MANTENIMIENTO_LOCAL',
    NOMINA = 'NOMINA',
    PUBLICIDAD = 'PUBLICIDAD',
    IMPUESTOS = 'IMPUESTOS',
    SUMINISTROS = 'SUMINISTROS',
    OTROS = 'OTROS'
}

// enums/estado-gasto.enum.ts
export enum EstadoGasto {
    PENDIENTE = 'PENDIENTE',
    PAGADO = 'PAGADO',
    ANULADO = 'ANULADO'
}

// enums/metodo-pago.enum.ts (ACTUALIZADO)
export enum MetodoPago {
    EFECTIVO = 'EFECTIVO',
    TARJETA = 'TARJETA',
    TRANSFERENCIA = 'TRANSFERENCIA',
    OTRO = 'OTRO' // <- NUEVO: Para gastos pagados con dinero externo
}

// models/gasto.model.ts
export interface GastoRequest {
    nombre: string;
    descripcion?: string;
    categoria: CategoriaGasto;
    monto: number;
    fechaGasto: string; // YYYY-MM-DD
    sucursalId: number;

    // Campos opcionales para "Pagar Ahora"
    pagarAhora?: boolean;
    metodoPago?: MetodoPago;
    bancoId?: number; // Requerido solo si metodoPago === TRANSFERENCIA
}

export interface PagarGastoRequest {
    metodoPago: MetodoPago;
    bancoId?: number;
}

export interface GastoResponse {
    id: number;
    nombre: string;
    descripcion: string;
    categoria: CategoriaGasto;
    monto: number;
    fechaGasto: string;
    estado: EstadoGasto;
    fechaPago?: string;
    metodoPago?: MetodoPago;
    sucursalId: number;
}
```

### 1.2 Endpoints (GastoService)
Base URL: `/gastos`

*   **Crear:** `POST /gastos`
    *   *Nota:* Si el usuario marca "Pagar Ahora" en el form, el backend procesa el pago automáticamente.
*   **Listar:** `GET /gastos?page=0&size=10`
*   **Pagar (Individual):** `POST /gastos/{id}/pagar` (Body: `PagarGastoRequest`)
*   **Eliminar:** `DELETE /gastos/{id}`

### 1.3 UI: Pantalla de Gastos
*   **Tabla Principal:** Lista los gastos con columnas (Nombre, Categoría, Monto, Fecha, Estado, Acciones).
    *   *Acción "Pagar":* Solo visible si Estado = PENDIENTE. Abre un diálogo pequeño para elegir Método de Pago.
*   **Botón "Nuevo Gasto":** Abre un diálogo.
    *   Formulario:
        1.  Datos del Gasto (Nombre, Categoría, Monto, Fecha).
        2.  Check: "¿Pagado?" (Si se activa, pide Método de Pago).
        3.  **VALIDACIÓN IMPORTANTE:**
            *   Si Método = `TRANSFERENCIA` -> Mostrar select de Bancos.
            *   Si Método = `EFECTIVO` -> Avisar textualmente "Se descontará de la caja actual".
            *   Si Método = `OTRO` -> Mostrar nota informativa: *"Gasto externo (No afecta caja ni bancos)"*.

---

## 2. Actualización: Equipamiento
El formulario de creación de equipamiento debe incluir la lógica del nuevo método de pago "OTRO".

### 2.1 Cambios en `EquipamientoRequest`
Asegúrate de que el modelo incluya:
```typescript
metodoPago?: MetodoPago; // Ahora acepta 'OTRO'
```

### 2.2 UI: Formulario de Equipamiento (Compra)
En la sección de "Datos Financieros" (si aplica):
*   Agregar opción **"OTRO / EXTERNO"** en el dropdown de Método de Pago.
*   **Comportamiento:**
    *   Si selecciona **OTRO**, oculta el selector de Bancos y no valida saldo de caja.
    *   Mostrar mensaje de ayuda: *"Use esta opción si el equipo fue comprado con fondos personales o externos al sistema."*

---

## 3. Resumen de Reglas de Negocio (Para validaciones en frontend)
1.  **OTRO:** Es un comodín. No requiere banco, no valida saldo, pero registra el gasto para reportes.
2.  **EFECTIVO:** Solo permitido si hay caja abierta (el backend valida saldo
3.  **TRANSFERENCIA:** Requiere seleccionar un Banco obligatoriamente.

Por favor, implementa estos cambios asegurando la consistencia con el diseño SaaS existente (Tablas PrimeNG, Diálogos modales).
