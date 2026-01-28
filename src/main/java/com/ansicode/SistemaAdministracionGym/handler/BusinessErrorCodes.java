package com.ansicode.SistemaAdministracionGym.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {

 // GENERAL
 NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "Código de error no definido"),
 VALIDATION_ERROR(1002, HttpStatus.BAD_REQUEST, "Error de validación"),

 // AUTH / SECURITY
 INCORRECT_PASSWORD(300, HttpStatus.BAD_REQUEST, "La contraseña es incorrecta"),
 NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden"),
 ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "La cuenta de usuario está bloqueada"),
 ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "La cuenta de usuario está deshabilitada"),
 BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Usuario o contraseña incorrectos"),

 // USERS (2100–2199)
 USER_NOT_FOUND(2100, HttpStatus.NOT_FOUND, "Usuario no encontrado"),
 USER_ALREADY_EXISTS(2101, HttpStatus.CONFLICT, "El usuario ya existe"),
 USER_PHONE_ALREADY_EXISTS(2102, HttpStatus.CONFLICT, "El número de teléfono ya está registrado"),
 USER_ROLE_REQUIRED(2103, HttpStatus.BAD_REQUEST, "Debe asignar al menos un rol"),
 USER_UNDERAGE(2104, HttpStatus.BAD_REQUEST, "El usuario debe ser mayor de 18 años"),
 USER_BIRTHDATE_INVALID(2105, HttpStatus.BAD_REQUEST, "La fecha de nacimiento no puede ser hoy ni una fecha futura"),


 // CLIENTES (2200–2299)
 CLIENTE_NOT_FOUND(2200, HttpStatus.NOT_FOUND, "Cliente no encontrado"),
 CLIENTE_CEDULA_ALREADY_EXISTS(2201, HttpStatus.CONFLICT, "La cédula ya está registrada"),
 CLIENTE_EMAIL_ALREADY_EXISTS(2202, HttpStatus.CONFLICT, "El email ya está registrado"),
 CLIENTE_PHONE_ALREADY_EXISTS(2203, HttpStatus.CONFLICT, "El teléfono ya está registrado"),
 CLIENTE_FECHA_NACIMIENTO_INVALIDA(2204, HttpStatus.BAD_REQUEST, "La fecha de nacimiento no puede ser hoy ni una fecha futura"),
 CLIENTE_UNDERAGE(2205, HttpStatus.BAD_REQUEST, "El cliente debe ser mayor de 18 años"),
 CLIENTE_DELETE_NOT_ALLOWED(2206, HttpStatus.CONFLICT, "No se puede eliminar el cliente porque tiene registros asociados"),
 CLIENTE_CODIGO_INTERNO_ERROR(2207, HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el código interno del cliente"),


 // ASISTENCIA (2300–2399)
 ASISTENCIA_CLIENTE_NOT_FOUND(2300, HttpStatus.NOT_FOUND, "Cliente no encontrado"),
 ASISTENCIA_DUPLICADA_HOY(2301, HttpStatus.CONFLICT, "El cliente ya tiene asistencia registrada hoy"),
 ASISTENCIA_SUSCRIPCION_NO_ACTIVA(2302, HttpStatus.BAD_REQUEST, "El cliente no tiene una suscripción activa o está vencida"),

 // CATEGORIA PRODUCTO (2400–2499)
 CATEGORIA_PRODUCTO_NOT_FOUND(2400, HttpStatus.NOT_FOUND, "Categoría no encontrada"),
 CATEGORIA_PRODUCTO_ALREADY_EXISTS(2401, HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre"),
 CATEGORIA_PRODUCTO_DELETE_NOT_ALLOWED(2402, HttpStatus.CONFLICT, "No se puede eliminar la categoría porque tiene productos asociados"),

 // CLIENTE SUSCRIPCIONES (2500–2599)
 SUSCRIPCION_VENTA_REQUIRED(2500, HttpStatus.BAD_REQUEST, "Venta requerida"),
 SUSCRIPCION_CLIENTE_REQUIRED(2501, HttpStatus.BAD_REQUEST, "Para suscripciones el cliente es obligatorio"),
 SUSCRIPCION_VENTA_SIN_DETALLE_SERVICIO(2502, HttpStatus.BAD_REQUEST, "Venta sin detalle de servicio"),
 SUSCRIPCION_SERVICIO_NOT_FOUND(2503, HttpStatus.NOT_FOUND, "Servicio no encontrado"),
 SUSCRIPCION_DURACION_INVALIDA(2504, HttpStatus.BAD_REQUEST, "Servicio de suscripción debe tener duracionDias válida"),
 SUSCRIPCION_NOT_FOUND(2505, HttpStatus.NOT_FOUND, "Suscripción no encontrada"),

 // COMPROBANTES (2600–2699)
 COMPROBANTE_VENTA_REQUIRED(2600, HttpStatus.BAD_REQUEST, "Venta requerida"),
 COMPROBANTE_NUMERO_FACTURA_REQUIRED(2601, HttpStatus.BAD_REQUEST, "La venta no tiene numeroFactura"),
 COMPROBANTE_VENTA_SIN_DETALLES(2602, HttpStatus.BAD_REQUEST, "No se puede generar comprobante: la venta no tiene detalles"),

 COMPROBANTE_NOT_FOUND(2603, HttpStatus.NOT_FOUND, "Comprobante no encontrado"),
 COMPROBANTE_PDF_NOT_GENERATED(2604, HttpStatus.BAD_REQUEST, "Este comprobante no tiene PDF generado"),
 COMPROBANTE_PDF_NOT_FOUND_OR_UNREADABLE(2605, HttpStatus.NOT_FOUND, "El archivo PDF no existe o no se puede leer"),

 COMPROBANTE_PDF_GENERATION_FAILED(2606, HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el PDF"),
 COMPROBANTE_PDF_SAVE_FAILED(2607, HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el PDF en disco"),
 COMPROBANTE_PDF_DOWNLOAD_FAILED(2608, HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo descargar el PDF"),

 // CONTEO CAJA / SESION CAJA (2700–2799)
 SESION_CAJA_NOT_FOUND(2700, HttpStatus.NOT_FOUND, "Sesión de caja no encontrada"),
 SESION_CAJA_CERRADA(2701, HttpStatus.CONFLICT, "No puedes registrar conteo en una sesión CERRADA"),
 CONTEO_CAJA_ITEMS_REQUIRED(2702, HttpStatus.BAD_REQUEST, "Debe enviar al menos un item para el conteo"),
 CONTEO_CAJA_ITEM_INVALIDO(2703, HttpStatus.BAD_REQUEST, "Item de conteo inválido"),
 CONTEO_CAJA_DENOMINACION_INVALIDA(2704, HttpStatus.BAD_REQUEST, "La denominación debe ser mayor a 0"),
 CONTEO_CAJA_CANTIDAD_INVALIDA(2705, HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor o igual a 0"),


 // CUADRE CAJA (2800–2899)
 CUADRE_SESION_CAJA_ID_REQUIRED(2800, HttpStatus.BAD_REQUEST, "sesionCajaId es obligatorio"),
 CUADRE_SESION_CAJA_NOT_FOUND(2801, HttpStatus.NOT_FOUND, "Sesión de caja no encontrada"),

 // DASHBOARD (2900–2999)
 DASHBOARD_RANGO_FECHAS_INVALIDO(2900, HttpStatus.BAD_REQUEST, "La fecha desde no puede ser mayor que hasta"),
 DASHBOARD_TIPO_INVALIDO(2901, HttpStatus.BAD_REQUEST, "tipo debe ser PRODUCTO o SERVICIO"),


 // MOVIMIENTO DINERO (3000–3099)
 MOVIMIENTO_DINERO_MONTO_INVALIDO(3000, HttpStatus.BAD_REQUEST, " El monto debe ser mayor a 0"),
 MOVIMIENTO_DINERO_TIPO_REQUIRED(3001, HttpStatus.BAD_REQUEST, " El tipo es obligatorio"),
 MOVIMIENTO_DINERO_CONCEPTO_REQUIRED(3002, HttpStatus.BAD_REQUEST, " El concepto es obligatorio"),
 MOVIMIENTO_DINERO_METODO_REQUIRED(3003, HttpStatus.BAD_REQUEST, " El metodo es obligatorio"),
 MOVIMIENTO_DINERO_SUCURSAL_REQUIRED(3004, HttpStatus.BAD_REQUEST, " La sucursalId es obligatorio"),

 MOVIMIENTO_DINERO_VENTA_NOT_FOUND(3005, HttpStatus.NOT_FOUND, "Venta no encontrada"),
 MOVIMIENTO_DINERO_PAGO_NOT_FOUND(3006, HttpStatus.NOT_FOUND, "Pago no encontrado"),
 MOVIMIENTO_DINERO_SERVICIO_NOT_FOUND(3007, HttpStatus.NOT_FOUND, "Servicio no encontrado"),

 MOVIMIENTO_DINERO_RANGO_FECHAS_INVALIDO(3008, HttpStatus.BAD_REQUEST, "La fecha desde no puede ser mayor que hasta"),


 // INVENTARIO (3100–3199)
 INVENTARIO_PRODUCTO_REQUIRED(3100, HttpStatus.BAD_REQUEST, "Producto requerido"),
 INVENTARIO_TIPO_REQUIRED(3101, HttpStatus.BAD_REQUEST, "Tipo de movimiento requerido"),
 INVENTARIO_VALOR_REQUIRED(3102, HttpStatus.BAD_REQUEST, "Valor requerido"),

 INVENTARIO_CANTIDAD_INVALIDA(3103, HttpStatus.BAD_REQUEST, "Cantidad debe ser mayor a 0"),
 INVENTARIO_STOCK_REAL_INVALIDO(3104, HttpStatus.BAD_REQUEST, "Stock real no puede ser negativo"),
 INVENTARIO_STOCK_INSUFICIENTE(3105, HttpStatus.CONFLICT, "Stock insuficiente"),

 INVENTARIO_RANGO_FECHAS_INVALIDO(3106, HttpStatus.BAD_REQUEST, "La fecha desde no puede ser mayor que hasta"),


 // PAGOS (3200–3299)
 PAGO_VENTA_ID_REQUIRED(3200, HttpStatus.BAD_REQUEST, "ventaId es obligatorio"),
 PAGO_METODO_REQUIRED(3201, HttpStatus.BAD_REQUEST, "metodo es obligatorio"),
 PAGO_TIPO_COMPROBANTE_REQUIRED(3202, HttpStatus.BAD_REQUEST, "tipoComprobante es obligatorio"),

 PAGO_VENTA_NOT_FOUND(3203, HttpStatus.NOT_FOUND, "Venta no encontrada"),
 PAGO_CLIENTE_NOT_FOUND(3204, HttpStatus.NOT_FOUND, "Cliente no encontrado"),

 PAGO_VENTA_ANULADA(3205, HttpStatus.BAD_REQUEST, "No se puede pagar una venta ANULADA"),
 PAGO_VENTA_REEMBOLSADA(3206, HttpStatus.BAD_REQUEST, "No se puede pagar una venta REEMBOLSADA"),
 PAGO_VENTA_YA_PAGADA(3207, HttpStatus.CONFLICT, "La venta ya está confirmada/pagada"),

 PAGO_VENTA_SIN_DETALLES(3208, HttpStatus.BAD_REQUEST, "No se puede pagar una venta sin detalles"),
 PAGO_VENTA_MIXTA_NO_SOPORTADA(3209, HttpStatus.BAD_REQUEST, "Esta venta es MIXTA. Usa el flujo mixto."),

 PAGO_CLIENTE_NO_COINCIDE_CON_VENTA(3210, HttpStatus.BAD_REQUEST, "El cliente del pago no coincide con el cliente de la venta"),
 PAGO_CLIENTE_REQUIRED_PARA_SERVICIOS(3211, HttpStatus.BAD_REQUEST, "Para servicios el cliente es obligatorio"),

 PAGO_VENTA_TOTAL_INVALIDO(3212, HttpStatus.BAD_REQUEST, "La venta no tiene un total válido"),
 PAGO_VENTA_SIN_SALDO_PENDIENTE(3213, HttpStatus.BAD_REQUEST, "La venta no tiene saldo pendiente"),

 PAGO_MONTO_INVALIDO(3214, HttpStatus.BAD_REQUEST, "El monto debe ser mayor a 0"),
 PAGO_MONTO_DEBE_SER_EXACTO(3215, HttpStatus.BAD_REQUEST, "Debes pagar el monto exacto pendiente"),

 PAGO_EFECTIVO_RECIBIDO_REQUIRED(3216, HttpStatus.BAD_REQUEST, "efectivoRecibido es obligatorio para EFECTIVO"),
 PAGO_EFECTIVO_INSUFICIENTE(3217, HttpStatus.BAD_REQUEST, "El efectivo recibido no puede ser menor al monto"),

 PAGO_SUCURSAL_REQUIRED(3218, HttpStatus.BAD_REQUEST, "La venta debe tener sucursal para registrar el movimiento de dinero"),

 PAGO_DETALLE_PRODUCTO_SIN_CANTIDAD(3219, HttpStatus.BAD_REQUEST, "Detalle de producto sin cantidad"),
 PAGO_DETALLE_PRODUCTO_CANTIDAD_NO_ENTERA(3220, HttpStatus.BAD_REQUEST, "La cantidad de producto debe ser entera"),
 PAGO_DETALLE_PRODUCTO_CANTIDAD_INVALIDA(3221, HttpStatus.BAD_REQUEST, "Cantidad de producto inválida"),
 PAGO_PRODUCTO_NOT_FOUND(3222, HttpStatus.NOT_FOUND, "Producto no encontrado"),

 PAGO_RANGO_FECHAS_INVALIDO(3223, HttpStatus.BAD_REQUEST, "La fecha desde no puede ser mayor que hasta"),

 // REPORTE PAGOS (3300–3399)
 REPORTE_PAGOS_RANGO_FECHAS_INVALIDO(3300, HttpStatus.BAD_REQUEST, "La fecha desde no puede ser mayor que hasta"),
 REPORTE_PAGOS_EXCEL_ERROR(3301, HttpStatus.INTERNAL_SERVER_ERROR, "Error generando Excel de pagos"),

 // PRODUCTOS (3400–3499)
 PRODUCTO_NOT_FOUND(3400, HttpStatus.NOT_FOUND, "Producto no encontrado"),
 PRODUCTO_CATEGORIA_REQUIRED(3401, HttpStatus.BAD_REQUEST, "La categoria del producto es obligatoria"),
 PRODUCTO_CATEGORIA_NOT_FOUND(3402, HttpStatus.NOT_FOUND, "Categoría no encontrada"),

 PRODUCTO_NOMBRE_REQUIRED(3403, HttpStatus.BAD_REQUEST, "El nombre del producto es obligatorio"),
 PRODUCTO_PRECIO_COMPRA_INVALIDO(3404, HttpStatus.BAD_REQUEST, "El precio de compra no es válido"),
 PRODUCTO_PRECIO_VENTA_INVALIDO(3405, HttpStatus.BAD_REQUEST, "El precio de venta no es válido"),

 PRODUCTO_STOCK_CANTIDAD_INVALIDA(3406, HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0"),
 PRODUCTO_SIN_PRECIO_COMPRA(3407, HttpStatus.BAD_REQUEST, "Producto sin precio de compra"),

 PRODUCTO_SUCURSAL_REQUIRED_PARA_EGRESO(3408, HttpStatus.BAD_REQUEST, "sucursalId es obligatorio cuando registrarEgreso = true"),
 PRODUCTO_METODO_PAGO_REQUIRED_PARA_EGRESO(3409, HttpStatus.BAD_REQUEST, "metodoPago es requerido cuando registrarEgreso = true"),

 PRODUCTO_AJUSTE_STOCK_REAL_INVALIDO(3410, HttpStatus.BAD_REQUEST, "El stock real no puede ser negativo"),

 // SERVICIOS (3500–3599)
 SERVICIO_NOT_FOUND(3500, HttpStatus.NOT_FOUND, "Servicio no encontrado"),

 SERVICIO_NOMBRE_REQUIRED(3501, HttpStatus.BAD_REQUEST, "El nombre del servicio es obligatorio"),
 SERVICIO_PRECIO_INVALIDO(3502, HttpStatus.BAD_REQUEST, "El precio del servicio no es válido"),

 SERVICIO_DURACION_REQUIRED_PARA_SUSCRIPCION(3503, HttpStatus.BAD_REQUEST, "Para suscripción duracionDias debe ser válida"),

 SERVICIO_FILTRO_SUSCRIPCION_REQUIRED(3504, HttpStatus.BAD_REQUEST, "El filtro suscripcion es obligatorio (true/false)"),

 // SESION CAJA (3600–3699)

 SESION_CAJA_ID_REQUIRED(3601, HttpStatus.BAD_REQUEST, "sesionCajaId es obligatorio"),
 SESION_CAJA_SUCURSAL_REQUIRED(3602, HttpStatus.BAD_REQUEST, "sucursalId es obligatorio"),
 SESION_CAJA_USUARIO_REQUIRED(3603, HttpStatus.BAD_REQUEST, "usuarioId es obligatorio"),

 SESION_CAJA_BASE_INICIAL_REQUIRED(3604, HttpStatus.BAD_REQUEST, "baseInicialEfectivo es obligatorio"),
 SESION_CAJA_BASE_INICIAL_INVALIDA(3605, HttpStatus.BAD_REQUEST, "baseInicialEfectivo no puede ser negativo"),

 SESION_CAJA_YA_ABIERTA_SUCURSAL(3606, HttpStatus.CONFLICT, "Ya existe una sesión de caja ABIERTA para esta sucursal"),
 SESION_CAJA_NO_ABIERTA_SUCURSAL(3607, HttpStatus.BAD_REQUEST, "No hay sesión de caja ABIERTA para esta sucursal"),
 SESION_CAJA_NO_ABIERTA_USUARIO(3608, HttpStatus.BAD_REQUEST, "No hay sesión de caja ABIERTA para este usuario"),

 SESION_CAJA_YA_CERRADA(3609, HttpStatus.CONFLICT, "La sesión ya está CERRADA"),
 SESION_CAJA_REQUIERE_CUADRE(3610, HttpStatus.BAD_REQUEST, "Debe realizar el cuadre antes de cerrar caja"),

 // SUCURSAL (3700–3799)
 SUCURSAL_YA_REGISTRADA(3700, HttpStatus.CONFLICT, "La sucursal ya está registrada"),

 SUCURSAL_NOT_FOUND(3701, HttpStatus.NOT_FOUND, "Sucursal no registrada"),
 SUCURSAL_NOT_FOUND_PARA_ACTUALIZAR(3702, HttpStatus.NOT_FOUND, "No hay sucursal registrada para actualizar"),

 SUCURSAL_HORARIO_REQUIRED(3703, HttpStatus.BAD_REQUEST, "horaApertura y horaCierre son obligatorias"),
 SUCURSAL_HORARIO_INVALIDO(3704, HttpStatus.BAD_REQUEST, "La hora de apertura debe ser menor a la hora de cierre"),


 // VENTAS (3800–3899)
 VENTA_SUCURSAL_REQUIRED(3800, HttpStatus.BAD_REQUEST, "sucursalId es obligatorio"),
 VENTA_SUCURSAL_NOT_FOUND(3801, HttpStatus.NOT_FOUND, "Sucursal no encontrada"),

 VENTA_CLIENTE_REQUIRED_PARA_SERVICIO(3802, HttpStatus.BAD_REQUEST, "clienteId es obligatorio para ventas de servicio"),
 VENTA_CLIENTE_NOT_FOUND(3803, HttpStatus.NOT_FOUND, "Cliente no encontrado"),

 VENTA_SERVICIO_REQUIRED(3804, HttpStatus.BAD_REQUEST, "servicioId es obligatorio"),
 VENTA_SERVICIO_NOT_FOUND(3805, HttpStatus.NOT_FOUND, "Servicio no encontrado"),
 VENTA_SERVICIO_PRECIO_INVALIDO(3806, HttpStatus.BAD_REQUEST, "El servicio no tiene un precio válido"),

 VENTA_ITEMS_REQUIRED(3807, HttpStatus.BAD_REQUEST, "items no puede estar vacío"),
 VENTA_ITEM_INVALIDO(3808, HttpStatus.BAD_REQUEST, "Item de producto inválido"),
 VENTA_PRODUCTO_REQUIRED(3809, HttpStatus.BAD_REQUEST, "productoId es obligatorio"),
 VENTA_PRODUCTO_NOT_FOUND(3810, HttpStatus.NOT_FOUND, "Producto no encontrado"),
 VENTA_PRODUCTO_PRECIO_INVALIDO(3811, HttpStatus.BAD_REQUEST, "El producto no tiene un precio de venta válido"),

 VENTA_CANTIDAD_INVALIDA(3812, HttpStatus.BAD_REQUEST, "cantidad debe ser mayor a 0"),
 VENTA_NUMERO_FACTURA_NO_UNICO(3813, HttpStatus.CONFLICT, "No se pudo generar un número de factura único");
















 @Getter
 private final int code;

 @Getter
 private final String description;

 @Getter
 private final HttpStatus httpStatus;

 BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
  this.code = code;
  this.httpStatus = httpStatus;
  this.description = description;
 }
}
