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
 USER_BIRTHDATE_INVALID(2105, HttpStatus.BAD_REQUEST, "La fecha de nacimiento no puede ser hoy ni una fecha futura");

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
