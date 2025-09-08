package com.josegc.loginchallenge.model;

import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
public class PhoneDto {
  @ToString.Exclude
  @Size(max = 50)
  Long number;

  @Size(max = 50)
  Integer cityCode;

  @Size(max = 50)
  String countryCode;
}
