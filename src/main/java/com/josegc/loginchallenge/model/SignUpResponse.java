package com.josegc.loginchallenge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.josegc.loginchallenge.model.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
public class SignUpResponse {
  UUID id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a")
  LocalDateTime created;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a")
  LocalDateTime lastLogin;

  String token;
  boolean isActive;

  public static SignUpResponse fromEntity(User user) {
    return SignUpResponse.builder()
        .id(user.getId())
        .created(user.getCreated())
        .lastLogin(user.getLastLogin())
        .token(user.getToken())
        .isActive(user.isActive())
        .build();
  }
}
