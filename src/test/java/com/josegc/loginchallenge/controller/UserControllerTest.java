package com.josegc.loginchallenge.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.josegc.JsonReader;
import com.josegc.loginchallenge.model.LoginRequest;
import com.josegc.loginchallenge.model.LoginResponse;
import com.josegc.loginchallenge.model.SignUpRequest;
import com.josegc.loginchallenge.model.SignUpResponse;
import com.josegc.loginchallenge.model.exception.BadClientException;
import com.josegc.loginchallenge.service.UserService;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @Test
  void testShouldSignUp() throws IOException {
    SignUpRequest request =
        JsonReader.readObject(SignUpRequest.class, "src/test/resources/user/success_sign_rq.json");
    SignUpResponse expected =
        JsonReader.readObject(SignUpResponse.class, "src/test/resources/user/success_sign_rs.json");

    when(userService.signUp(request)).thenReturn(expected);

    ResponseEntity<SignUpResponse> actual = userController.signUp(request);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertEquals(expected, actual.getBody());
  }

  @Test
  void testShouldFailMalformedPassword() throws IOException {
    SignUpRequest request =
        JsonReader.readObject(SignUpRequest.class, "src/test/resources/user/success_sign_rq.json")
            .withPassword(UUID.randomUUID() + "123");
    assertThrows(BadClientException.class, () -> userController.signUp(request));
  }

  @Test
  void testShouldLogIn() throws IOException {
    LoginRequest request =
        JsonReader.readObject(LoginRequest.class, "src/test/resources/user/success_login_rq.json");
    LoginResponse expected =
        JsonReader.readObject(LoginResponse.class, "src/test/resources/user/success_login_rs.json");

    String token = UUID.randomUUID().toString();
    when(userService.login(token, request)).thenReturn(expected);

    ResponseEntity<LoginResponse> actual = userController.login(token, request);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertEquals(expected, actual.getBody());
  }
}
