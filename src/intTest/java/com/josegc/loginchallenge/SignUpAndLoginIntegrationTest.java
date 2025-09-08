package com.josegc.loginchallenge;

import static com.josegc.loginchallenge.Main.MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.josegc.loginchallenge.model.LoginRequest;
import com.josegc.loginchallenge.model.SignUpRequest;
import com.josegc.loginchallenge.model.SignUpResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SignUpAndLoginIntegrationTest {

  private static final String SIGN_UP_URL = "/api/user/sign-up";
  private static final String LOGIN_URL = "/api/user/login";

  @Autowired private TestRestTemplate restTemplate;

  private ResponseEntity<String> signUp(SignUpRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    var entity = new HttpEntity<>(request, headers);
    return restTemplate.postForEntity(SIGN_UP_URL, entity, String.class);
  }

  private ResponseEntity<String> login(LoginRequest request, String authorization) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + authorization);
    var entity = new HttpEntity<>(request, headers);
    return restTemplate.postForEntity(LOGIN_URL, entity, String.class);
  }

  private static SignUpRequest readSignUpRq() {
    String requestBody;
    try (Stream<String> lines = Files.lines(Path.of("src/intTest/resources/jsons/rq.json"))) {
      requestBody = lines.collect(Collectors.joining());
    } catch (IOException ioEx) {
      throw new UncheckedIOException(ioEx);
    }
    try {
      return MAPPER.readValue(requestBody, SignUpRequest.class).withEmail(getEmail());
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String decodeJwt(String jwt) {
    return new String(Base64.getDecoder().decode(jwt.split("\\.")[1]));
  }

  private static <T> T map(String body, Class<T> clazz) {
    try {
      return MAPPER.readValue(body, clazz);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void testSignUpIntegration() {
    SignUpRequest signUpRequest = readSignUpRq();
    ResponseEntity<String> entityRs = signUp(signUpRequest);
    assertEquals(HttpStatus.OK, entityRs.getStatusCode());
    SignUpResponse signUpResponse = map(entityRs.getBody(), SignUpResponse.class);
    String decodedClaims = decodeJwt(signUpResponse.getToken());
    assertTrue(decodedClaims.contains(signUpRequest.getEmail()));
  }

  private static String getEmail() {
    return UUID.randomUUID() + "@" + UUID.randomUUID() + ".com";
  }

  @Test
  void testLoginIntegration() {
    SignUpRequest signUpRequest = readSignUpRq();
    ResponseEntity<String> entityRs = signUp(signUpRequest);
    assertEquals(HttpStatus.OK, entityRs.getStatusCode());
    SignUpResponse signUpResponse = map(entityRs.getBody(), SignUpResponse.class);
    ResponseEntity<String> loginResponse =
        login(
            LoginRequest.builder().password(signUpRequest.getPassword()).build(),
            signUpResponse.getToken());
    assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
  }

  @Test
  @Disabled("Race condition between find and save.")
  void testLoginShouldChangePassword() {
    SignUpRequest signUpRequest = readSignUpRq();
    ResponseEntity<String> entityRs = signUp(signUpRequest);
    assertEquals(HttpStatus.OK, entityRs.getStatusCode());
    SignUpResponse signUpResponse = map(entityRs.getBody(), SignUpResponse.class);
    login(
        LoginRequest.builder().password(signUpRequest.getPassword()).build(),
        signUpResponse.getToken());
    var loginResponse =
        login(
            LoginRequest.builder().password(signUpRequest.getPassword()).build(),
            signUpResponse.getToken());
    assertEquals(HttpStatus.FORBIDDEN, loginResponse.getStatusCode());
  }
}
