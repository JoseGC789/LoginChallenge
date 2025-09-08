package com.josegc.loginchallenge.service;

import com.josegc.JsonReader;
import com.josegc.loginchallenge.model.LoginRequest;
import com.josegc.loginchallenge.model.LoginResponse;
import com.josegc.loginchallenge.model.SignUpRequest;
import com.josegc.loginchallenge.model.SignUpResponse;
import com.josegc.loginchallenge.model.entity.User;
import com.josegc.loginchallenge.model.exception.BadClientException;
import com.josegc.loginchallenge.model.exception.SecurityAuthenticationException;
import com.josegc.loginchallenge.model.exception.SecurityForbiddenException;
import com.josegc.loginchallenge.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SigningAndLoginServiceTest {

  private static final String RANDOM_STRING = UUID.randomUUID().toString();
  @Captor private ArgumentCaptor<User> userCaptor;
  @Mock private UserRepository userRepository;
  @Mock private PasswordService passwordService;
  @Mock private JwtUtil jwtUtil;
  @InjectMocks private SigningAndLoginService signingAndLoginService;

  @Test
  void testExistingEmailBadRequests() throws IOException {
    SignUpRequest request =
        JsonReader.readObject(SignUpRequest.class, "src/test/resources/user/success_sign_rq.json");
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(Boolean.TRUE);
    assertThrows(BadClientException.class, () -> signingAndLoginService.signUp(request));
  }

  @Test
  void testShouldSingUpUser() throws IOException {
    SignUpRequest request =
        JsonReader.readObject(SignUpRequest.class, "src/test/resources/user/success_sign_rq.json");

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(passwordService.protect(eq(request.getPassword()), any())).thenReturn(RANDOM_STRING);
    when(jwtUtil.generate(request.getEmail())).thenReturn(RANDOM_STRING);

    SignUpResponse response = signingAndLoginService.signUp(request);

    assertNotNull(response.getToken());
    assertNotNull(response.getId());
    assertNotNull(response.getCreated());
    assertNotNull(response.getLastLogin());
    verify(userRepository).save(userCaptor.capture());
    assertNotNull(userCaptor.getValue().getName());
    assertNotNull(userCaptor.getValue().getEmail());
    assertNotNull(userCaptor.getValue().getSalt());
    assertNotNull(userCaptor.getValue().getPassword());
    assertTrue(userCaptor.getValue().isActive());
    assertTrue(userCaptor.getValue().getPhones().stream().anyMatch(Objects::nonNull));
    assertNotNull(userCaptor.getValue().getRoles());
  }

  @Test
  void testShouldThrowIfNoAuthentication() {
    when(jwtUtil.extractSubject(RANDOM_STRING)).thenThrow(new RuntimeException());
    assertThrows(
        SecurityAuthenticationException.class,
        () -> signingAndLoginService.login(RANDOM_STRING, null));
  }

  @Test
  void testShouldForbidLoginWhenNotFound() throws IOException {
    User user = JsonReader.readObject(User.class, "src/test/resources/user/entity_user.json");
    when(jwtUtil.extractSubject(RANDOM_STRING)).thenReturn(user.getEmail());
    when(userRepository.findByEmailAndTokenAndIsActiveTrue(user.getEmail(), RANDOM_STRING))
        .thenReturn(Optional.empty());
    assertThrows(
        SecurityForbiddenException.class, () -> signingAndLoginService.login(RANDOM_STRING, null));
  }

  @Test
  void testShouldForbidLoginWhenPasswordNotMatching() throws IOException {
    User user = JsonReader.readObject(User.class, "src/test/resources/user/entity_user.json");
    when(jwtUtil.extractSubject(RANDOM_STRING)).thenReturn(user.getEmail());
    when(userRepository.findByEmailAndTokenAndIsActiveTrue(user.getEmail(), RANDOM_STRING))
        .thenReturn(Optional.of(user));
    when(passwordService.verify(RANDOM_STRING, user.getSalt(), user.getPassword()))
        .thenReturn(false);
    LoginRequest rq = LoginRequest.builder().password(RANDOM_STRING).build();
    assertThrows(
        SecurityForbiddenException.class,
        () ->
            signingAndLoginService.login(
                RANDOM_STRING, rq));
  }

  @Test
  void testShouldLoginUserChangingJwt() throws IOException {
    User user = JsonReader.readObject(User.class, "src/test/resources/user/entity_user.json");
    String oldToken = user.getToken();
    LoginRequest loginRequest =
        JsonReader.readObject(LoginRequest.class, "src/test/resources/user/success_login_rq.json");
    user.setActive(false);
    user.setLastLogin(null);
    when(jwtUtil.extractSubject(RANDOM_STRING)).thenReturn(user.getEmail());
    when(userRepository.findByEmailAndTokenAndIsActiveTrue(user.getEmail(), RANDOM_STRING))
        .thenReturn(Optional.of(user));
    when(passwordService.verify(loginRequest.getPassword(), user.getSalt(), user.getPassword()))
        .thenReturn(true);
    when(jwtUtil.generate(user.getEmail())).thenReturn(RANDOM_STRING);
    LoginResponse actual = signingAndLoginService.login(RANDOM_STRING, loginRequest);

    assertTrue(user.isActive());
    assertNotNull(user.getLastLogin());
    assertNotEquals(oldToken, user.getToken());
    assertNotNull(actual);
    verify(userRepository).save(any());
  }
}
