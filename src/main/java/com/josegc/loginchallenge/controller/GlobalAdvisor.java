package com.josegc.loginchallenge.controller;

import static java.util.Objects.requireNonNullElse;

import com.josegc.loginchallenge.model.exception.BadClientException;
import com.josegc.loginchallenge.model.exception.InternalException;
import com.josegc.loginchallenge.model.exception.NoResourceException;
import com.josegc.loginchallenge.model.exception.SecurityAuthenticationException;
import com.josegc.loginchallenge.model.exception.SecurityForbiddenException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalAdvisor {
  private static final Supplier<String> GENERIC = () -> UUID.randomUUID().toString();
  private static final FieldError NULL_FIELD =
      new FieldError(GENERIC.get(), GENERIC.get(), GENERIC.get());

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(Exception e) {
    return ResponseEntity.internalServerError()
        .body(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
    return ResponseEntity.internalServerError()
        .body(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleInvalid(MethodArgumentNotValidException e) {
    FieldError errorField = requireNonNullElse(e.getFieldError(), NULL_FIELD);

    return ResponseEntity.badRequest()
        .body(
            buildResponse(
                HttpStatus.BAD_REQUEST,
                errorField.getField() + " " + errorField.getDefaultMessage()));
  }

  @ExceptionHandler(BadClientException.class)
  public ResponseEntity<Map<String, Object>> handleBadClient(BadClientException ex) {
    return ResponseEntity.status(BadClientException.STATUS)
        .body(buildResponse(BadClientException.STATUS, ex));
  }

  @ExceptionHandler(SecurityForbiddenException.class)
  public ResponseEntity<Map<String, Object>> handleForbidden(SecurityForbiddenException ex) {
    return ResponseEntity.status(SecurityForbiddenException.STATUS)
        .body(buildResponse(SecurityForbiddenException.STATUS, ex));
  }

  @ExceptionHandler(SecurityAuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handleForbidden(SecurityAuthenticationException ex) {
    return ResponseEntity.status(SecurityAuthenticationException.STATUS)
        .body(buildResponse(SecurityAuthenticationException.STATUS, ex));
  }

  @ExceptionHandler(NoResourceException.class)
  public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceException ex) {
    return ResponseEntity.status(NoResourceException.STATUS)
        .body(buildResponse(NoResourceException.STATUS, ex));
  }

  @ExceptionHandler(InternalException.class)
  public ResponseEntity<Map<String, Object>> handleInternal(InternalException ex) {
    return ResponseEntity.status(InternalException.STATUS)
        .body(buildResponse(InternalException.STATUS, ex));
  }

  private Map<String, Object> buildResponse(HttpStatus status, Exception ex) {
    return buildResponse(
        status, Optional.ofNullable(ex).map(Exception::getMessage).orElse(GENERIC.get()));
  }

  private Map<String, Object> buildResponse(HttpStatus status, String message) {
    Map<String, Object> body = new HashMap<>();
    body.put(
        "error",
        List.of(
            Map.of(
                "timestamp", LocalDateTime.now(),
                "codigo", status.value(),
                "detail", message)));
    return body;
  }
}
