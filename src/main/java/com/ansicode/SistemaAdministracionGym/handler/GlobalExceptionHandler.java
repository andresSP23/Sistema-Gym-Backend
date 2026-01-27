package com.ansicode.SistemaAdministracionGym.handler;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // ============================
  // AUTH / SECURITY
  // ============================

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ExceptionResponse.builder()
                    .businessErrorCode(BusinessErrorCodes.ACCOUNT_LOCKED.getCode())
                    .businessErrorDescription(BusinessErrorCodes.ACCOUNT_LOCKED.getDescription())
                    .error(exp.getMessage())
                    .build());
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ExceptionResponse.builder()
                    .businessErrorCode(BusinessErrorCodes.ACCOUNT_DISABLED.getCode())
                    .businessErrorDescription(BusinessErrorCodes.ACCOUNT_DISABLED.getDescription())
                    .error(exp.getMessage())
                    .build());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ExceptionResponse.builder()
                    .businessErrorCode(BusinessErrorCodes.BAD_CREDENTIALS.getCode())
                    .businessErrorDescription(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                    .error(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                    .build());
  }

  // ============================
  // BUSINESS ERRORS (TU APP)
  // ============================

  @ExceptionHandler(BussinessException.class)
  public ResponseEntity<ExceptionResponse> handleException(BussinessException exp) {

    BusinessErrorCodes code = exp.getErrorCode();

    // ✅ fallback si viene null (para que nunca reviente)
    if (code == null) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(ExceptionResponse.builder()
                      .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                      .businessErrorDescription("Business error")
                      .error(exp.getMessage() != null ? exp.getMessage() : "Business rule violated")
                      .build());
    }

    HttpStatus status = code.getHttpStatus() != null ? code.getHttpStatus() : HttpStatus.BAD_REQUEST;

    return ResponseEntity
            .status(status)
            .body(ExceptionResponse.builder()
                    .businessErrorCode(code.getCode())
                    .businessErrorDescription(code.getDescription())
                    .error(exp.getMessage() != null ? exp.getMessage() : code.getDescription())
                    .build());
  }

  // ============================
  // VALIDATION (DTO @Valid)
  // ============================

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {

    // lista simple
    Set<String> validationErrors = new HashSet<>();

    // mapa por campo (mejor para Angular forms)
    Map<String, String> fieldErrors = new HashMap<>();

    exp.getBindingResult().getFieldErrors().forEach(err -> {
      String field = err.getField();
      String message = err.getDefaultMessage();
      fieldErrors.put(field, message);
      validationErrors.add(message);
    });

    // por si hay errores globales (no asociados a un field)
    exp.getBindingResult().getGlobalErrors().forEach(err -> {
      String message = err.getDefaultMessage();
      validationErrors.add(message);
    });

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse.builder()
                    .businessErrorCode(BusinessErrorCodes.VALIDATION_ERROR.getCode())
                    .businessErrorDescription(BusinessErrorCodes.VALIDATION_ERROR.getDescription())
                    .validationErrors(validationErrors)
                    .errors(fieldErrors)
                    .build());
  }

  // ✅ cuando validas params/path/query con @Validated (no DTO)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ExceptionResponse> handleException(ConstraintViolationException exp) {

    Set<String> validationErrors = new HashSet<>();
    exp.getConstraintViolations().forEach(v -> validationErrors.add(v.getMessage()));

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse.builder()
                    .businessErrorCode(BusinessErrorCodes.VALIDATION_ERROR.getCode())
                    .businessErrorDescription(BusinessErrorCodes.VALIDATION_ERROR.getDescription())
                    .validationErrors(validationErrors)
                    .error("Validation error")
                    .build());
  }

  // ============================
  // NOT FOUND
  // ============================

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException exp) {
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse.builder()
                    .businessErrorDescription("Not Found")
                    .error(exp.getMessage())
                    .build());
  }

  // ============================
  // DB CONSTRAINTS
  // ============================

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ExceptionResponse> handleException(DataIntegrityViolationException exp) {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ExceptionResponse.builder()
                    .businessErrorDescription("Data integrity violation")
                    .error("No se pudo completar la operación por una restricción de datos (por ejemplo, valores duplicados).")
                    .build());
  }

  // ============================
  // EMAIL / INFRA
  // ============================

  @ExceptionHandler(MessagingException.class)
  public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionResponse.builder()
                    .error(exp.getMessage())
                    .build());
  }

  // ============================
  // GENERIC (AL FINAL)
  // ============================

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleException(Exception exp) {

    exp.printStackTrace();

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionResponse.builder()
                    .businessErrorDescription("Internal Server Error")
                    .error(exp.getMessage())
                    .build());
  }
}
