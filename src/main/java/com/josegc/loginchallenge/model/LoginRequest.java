package com.josegc.loginchallenge.model;

import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
public class LoginRequest {
  @ToString.Exclude @NotEmpty String password;
}
