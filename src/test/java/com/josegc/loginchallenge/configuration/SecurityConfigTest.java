package com.josegc.loginchallenge.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

  @Test
  void testShouldReturnAuthenticationManager() throws Exception {
    AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
    AuthenticationManager fakeManager = mock(AuthenticationManager.class);
    when(config.getAuthenticationManager()).thenReturn(fakeManager);
    assertNotNull(securityConfig.authenticationManager(config));
  }
}
