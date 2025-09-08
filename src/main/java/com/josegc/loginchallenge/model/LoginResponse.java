package com.josegc.loginchallenge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
public class LoginResponse {
  UUID id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a")
  LocalDateTime created;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a")
  LocalDateTime lastLogin;

  String token;
  boolean isActive;
  String name;
  @ToString.Exclude String email;
  @ToString.Exclude String password;
  List<PhoneDto> phones;
}
