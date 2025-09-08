package com.josegc.loginchallenge.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JwtUtil {

  private static final int ONE_HOUR = 1000 * 60 * 60;

  @Value("${app.security.jwt.secret}")
  private final String secret;

  public String generate(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ONE_HOUR))
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
  }

  public String extractSubject(String token) {
    return extractAllClaims(token).getSubject();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }
}
