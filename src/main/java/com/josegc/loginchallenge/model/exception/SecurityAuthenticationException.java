package com.josegc.loginchallenge.model.exception;

import lombok.Getter;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

@Getter
@StandardException
public class SecurityAuthenticationException extends RuntimeException {
  public static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
}
