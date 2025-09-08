package com.josegc.loginchallenge.configuration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

  @InjectMocks private SecurityConfig securityConfig;

  @Test
  void testShouldEncode() {
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    String raw = UUID.randomUUID().toString();
    assertTrue(encoder.matches(raw, encoder.encode(raw)));
  }
}
