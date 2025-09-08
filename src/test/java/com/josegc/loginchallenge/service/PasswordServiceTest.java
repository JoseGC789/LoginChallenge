package com.josegc.loginchallenge.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {
  private static final String SECRET = UUID.randomUUID().toString();
  @Mock private PasswordEncoder passwordEncoder;
  private PasswordService passwordService;

  @BeforeEach
  void setUp() {
    passwordService = new PasswordService(passwordEncoder, SECRET);
  }

  @Test
  void testShouldProtectAndVerify() {
    String password = UUID.randomUUID().toString();
    String salt = UUID.randomUUID().toString();

    String encodedValue = "encoded-" + password + salt;
    when(passwordEncoder.encode(password + salt)).thenReturn(encodedValue);
    when(passwordEncoder.matches(password + salt, encodedValue)).thenReturn(true);

    String encrypted = passwordService.protect(password, salt);
    assertNotNull(encrypted);
    assertFalse(encrypted.isEmpty());

    boolean actual = passwordService.verify(password, salt, encrypted);
    assertTrue(actual);
  }

  @Test
  void testShouldProtectButNotVerifyWithWrongPassword() {
    String correctPassword = UUID.randomUUID().toString();
    String wrongPassword = UUID.randomUUID().toString();
    String salt = UUID.randomUUID().toString();

    String encodedValue = "encoded-" + correctPassword + salt;
    when(passwordEncoder.encode(correctPassword + salt)).thenReturn(encodedValue);
    when(passwordEncoder.matches(wrongPassword + salt, encodedValue)).thenReturn(false);

    String encrypted = passwordService.protect(correctPassword, salt);

    assertFalse(passwordService.verify(wrongPassword, salt, encrypted));
  }

  @Test
  void testShouldSneakyThrow() {
    String salt = UUID.randomUUID().toString();
    String salt2 = UUID.randomUUID().toString();
    assertThrows(RuntimeException.class, () -> passwordService.protect(null, salt));
    assertThrows(RuntimeException.class, () -> passwordService.verify(null, salt2, null));
  }
}
