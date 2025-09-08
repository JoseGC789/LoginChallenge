package com.josegc.loginchallenge.service;

import com.josegc.loginchallenge.model.LoginRequest;
import com.josegc.loginchallenge.model.LoginResponse;
import com.josegc.loginchallenge.model.SignUpRequest;
import com.josegc.loginchallenge.model.SignUpResponse;

public interface UserService {
  SignUpResponse signUp(SignUpRequest request);

  LoginResponse login(String token, LoginRequest request);
}
