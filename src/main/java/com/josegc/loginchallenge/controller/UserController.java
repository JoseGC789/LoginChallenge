package com.josegc.loginchallenge.controller;

import com.josegc.loginchallenge.model.LoginRequest;
import com.josegc.loginchallenge.model.LoginResponse;
import com.josegc.loginchallenge.model.SignUpRequest;
import com.josegc.loginchallenge.model.SignUpResponse;
import com.josegc.loginchallenge.model.exception.BadClientException;
import com.josegc.loginchallenge.service.UserService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Validated
public class UserController {

  private static final int PASSWORD_MAX_NUMBERS = 2;
  private final UserService userService;

  @PostMapping("/sign-up")
  public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
    if (PASSWORD_MAX_NUMBERS < request.getPassword().replaceAll("[a-zA-Z]", "").length()) {
      throw new BadClientException("Password must be well-formed");
    }

    return ResponseEntity.ok(userService.signUp(request));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @RequestHeader(value = "Authorization") String authorization,
      @RequestBody LoginRequest request) {
    String token = authorization.replace("Bearer ", "");
    return ResponseEntity.ok(userService.login(token, request));
  }
}
