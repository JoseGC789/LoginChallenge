package com.josegc.loginchallenge.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {
  private static final String FIXED_SECRET = "5edd5e3f-cbbd-405c-a113-b1909d802936";
  private static final String EXPIRED_JWT =
      "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5NTBhMWQzYS1mOTk5LTRiODktOTEzNi1lMjNiMzdjODcxYzBANDQ2NDA0MzQtYWEwNy00YWY3LTlhZjItNWM4ZTgyNDNkYzUzLmNvbSIsImlhdCI6MTc1NzI4MzM1MSwiZXhwIjoxNzU3MjgzMzUxfQ.cDZS0yRJE2H0sohermijKc65-GY6RL3mRapJUUEUGKY";
  private static final String ENDLESS_JWT =
      "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwNmYwMDEyZS0xOGFkLTQwZjgtODgyMS02ZGJhMzA0ZThkYzZAYjljNGJiYmYtOTVjNi00Zjk2LWJmMGQtMGE1YTQxNDRiYzY3LmNvbSIsImlhdCI6MTc1NzI4MzUxMn0.y8GujNOC5sVYWhqLWaR_HxeNZO23hTjJTu-LHs32yTU";
  private static final String OTHER_SIGNATURE_EXPIRED_JWT =
      "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlNzE1NWRkOC03ZTAzLTQ5MzUtYWVjNS04MzljMzJmNWZkYWFANzZhNTEyMGYtNDVhNy00Y2EzLTgxMmItNmQ0OWE2ZDIxNmFjLmNvbSIsImlhdCI6MTc1NzI4MzEwOCwiZXhwIjoxNzU3MjgzMTA4fQ.O_kGPH7oOwLdm1FtfyKVY2HUTS0OnPxKBkAkzlsftRw";
  private JwtUtil jwtUtil;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil(FIXED_SECRET);
  }

  @Test
  void testShouldGenerateJwt() {
    String expectedEmail = UUID.randomUUID() + "@" + UUID.randomUUID() + ".com";
    String jwt = jwtUtil.generate(expectedEmail);
    String[] parts = jwt.split("\\.");
    String actual = new String(Base64.getDecoder().decode(parts[1]));
    assertTrue(actual.contains(expectedEmail));
  }

  @Test
  void testShouldGenerateValidUnExpiredJwt() {
    String expected = UUID.randomUUID().toString();
    String token = jwtUtil.generate(expected);
    String actual = jwtUtil.extractSubject(token);
    assertEquals(expected, actual);
  }

  @Test
  void testShouldFailWrongSignature() {
    assertThrows(
        SignatureException.class, () -> jwtUtil.extractSubject(OTHER_SIGNATURE_EXPIRED_JWT));
  }

  @Test
  void testShouldFailDueToExpired() {
    assertThrows(ExpiredJwtException.class, () -> jwtUtil.extractSubject(EXPIRED_JWT));
  }

  @Test
  void testShouldExtractSubjectBeingEmail() {
    String actual = jwtUtil.extractSubject(ENDLESS_JWT);
    assertEquals(
        "06f0012e-18ad-40f8-8821-6dba304e8dc6@b9c4bbbf-95c6-4f96-bf0d-0a5a4144bc67.com", actual);
  }
}
