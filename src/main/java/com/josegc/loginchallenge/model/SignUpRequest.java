package com.josegc.loginchallenge.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
public class SignUpRequest {

  @NotEmpty
  @Size(max = 36)
  String name;

  @ToString.Exclude
  @Size(max = 255)
  @Email
  @NotEmpty
  String email;

  @ToString.Exclude
  @Pattern(regexp = "[a-zA-Z0-9]{8,12}", message = "must be well-formed")
  @NotEmpty
  String password;

  @Builder.Default List<PhoneDto> phones = new ArrayList<>();
}
