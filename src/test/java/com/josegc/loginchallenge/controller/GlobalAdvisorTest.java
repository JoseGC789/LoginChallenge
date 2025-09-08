package com.josegc.loginchallenge.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.josegc.loginchallenge.model.exception.BadClientException;
import com.josegc.loginchallenge.model.exception.InternalException;
import com.josegc.loginchallenge.model.exception.NoResourceException;
import com.josegc.loginchallenge.model.exception.SecurityAuthenticationException;
import com.josegc.loginchallenge.model.exception.SecurityForbiddenException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith({MockitoExtension.class})
class GlobalAdvisorTest {

  @InjectMocks private GlobalAdvisor advisor;

  @Test
  void testShouldReturnGenericException() {
    Exception ex = new Exception(UUID.randomUUID().toString());
    ResponseEntity<Map<String, Object>> actual = advisor.handleRuntimeException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actual.getStatusCode());
    assertEquals(ex.getMessage(), getDetail(actual));
  }

  @Test
  void testShouldReturnRuntimeException() {
    RuntimeException ex = new RuntimeException(UUID.randomUUID().toString());
    ResponseEntity<Map<String, Object>> actual = advisor.handleRuntimeException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actual.getStatusCode());
    assertEquals(ex.getMessage(), getDetail(actual));
  }

  @Test
  void testShouldReturnBadClientException() {
    BadClientException ex = new BadClientException(UUID.randomUUID().toString());
    ResponseEntity<Map<String, Object>> actual = advisor.handleBadClient(ex);

    assertEquals(BadClientException.STATUS, actual.getStatusCode());
    assertEquals(ex.getMessage(), getDetail(actual));
  }

  @Test
  void testShouldReturnSecurityForbiddenException() {
    SecurityForbiddenException ex = new SecurityForbiddenException(UUID.randomUUID().toString());
    ResponseEntity<Map<String, Object>> actual = advisor.handleForbidden(ex);

    assertEquals(SecurityForbiddenException.STATUS, actual.getStatusCode());
    assertEquals(ex.getMessage(), getDetail(actual));
  }

  @Test
  void testShouldReturnSecurityAuthenticationException() {
    SecurityAuthenticationException ex =
        new SecurityAuthenticationException(UUID.randomUUID().toString());
    ResponseEntity<Map<String, Object>> actual = advisor.handleForbidden(ex);

    assertEquals(SecurityAuthenticationException.STATUS, actual.getStatusCode());
    assertEquals(ex.getMessage(), getDetail(actual));
  }

  @Test
  void testShouldReturnNoResourceException() {
    NoResourceException ex = new NoResourceException(UUID.randomUUID().toString());
    ResponseEntity<Map<String, Object>> actual = advisor.handleNoResource(ex);

    assertEquals(NoResourceException.STATUS, actual.getStatusCode());
    assertEquals(ex.getMessage(), getDetail(actual));
  }

  @Test
  void testShouldReturnInternalException() {
    InternalException ex = new InternalException(UUID.randomUUID().toString());
    ResponseEntity<Map<String, Object>> actual = advisor.handleInternal(ex);

    assertEquals(InternalException.STATUS, actual.getStatusCode());
    assertEquals(ex.getMessage(), getDetail(actual));
  }

  @Test
  void testShouldReturnMethodInvalid() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    var fieldError = mock(org.springframework.validation.FieldError.class);

    when(ex.getFieldError()).thenReturn(fieldError);
    when(fieldError.getField()).thenReturn("username");
    when(fieldError.getDefaultMessage()).thenReturn("must not be blank");

    ResponseEntity<Map<String, Object>> actual = advisor.handleInvalid(ex);

    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertTrue(getDetail(actual).contains("username must not be blank"));
  }

  @Test
  void testShouldReturnGenericMethodInvalid() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

    when(ex.getFieldError()).thenReturn(null);

    ResponseEntity<Map<String, Object>> actual = advisor.handleInvalid(ex);

    assertEquals(
        getDetail(actual).length(),
        UUID.randomUUID().toString().length()
            + " ".length()
            + UUID.randomUUID().toString().length());
  }

  @SuppressWarnings("unchecked")
  private String getDetail(ResponseEntity<Map<String, Object>> response) {
    var errorList = (List<Map<String, Object>>) response.getBody().get("error");
    return (String) errorList.get(0).get("detail");
  }
}
