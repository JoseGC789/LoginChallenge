package com.josegc.loginchallenge.service;

import static com.josegc.loginchallenge.Main.MAPPER;

import com.fasterxml.jackson.core.type.TypeReference;
import com.josegc.loginchallenge.model.LoginRequest;
import com.josegc.loginchallenge.model.LoginResponse;
import com.josegc.loginchallenge.model.SignUpRequest;
import com.josegc.loginchallenge.model.SignUpResponse;
import com.josegc.loginchallenge.model.entity.Phone;
import com.josegc.loginchallenge.model.entity.Role;
import com.josegc.loginchallenge.model.entity.User;
import com.josegc.loginchallenge.model.exception.BadClientException;
import com.josegc.loginchallenge.model.exception.SecurityAuthenticationException;
import com.josegc.loginchallenge.model.exception.SecurityForbiddenException;
import com.josegc.loginchallenge.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SigningAndLoginService implements UserService {

  private static final String FORBIDDEN_MESSAGE = "Try again";
  private final UserRepository userRepository;
  private final PasswordService passwordService;
  private final JwtUtil jwtUtil;

  @Override
  public SignUpResponse signUp(SignUpRequest request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadClientException("Can't");
    }

    List<Phone> phones =
        request.getPhones().stream()
            .map(phoneRequest -> MAPPER.convertValue(phoneRequest, new TypeReference<Phone>() {}))
            .collect(Collectors.toList());

    String salt = UUID.randomUUID().toString();

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setSalt(salt);
    user.setPassword(passwordService.protect(request.getPassword(), salt));
    user.setCreated(LocalDateTime.now());
    user.setLastLogin(LocalDateTime.now());
    user.setToken(jwtUtil.generate(request.getEmail()));
    user.setActive(true);
    user.setPhones(phones);
    user.setRoles(Set.of(Role.USER));

    userRepository.save(user);

    return SignUpResponse.fromEntity(user);
  }

  @Override
  @Transactional(timeout = 5)
  public LoginResponse login(String token, LoginRequest request) {
    String email;
    try {
      email = jwtUtil.extractSubject(token);
    } catch (Exception e) {
      throw new SecurityAuthenticationException("Please authenticate", e);
    }

    User user =
        userRepository
            .findByEmailAndTokenAndIsActiveTrue(email, token)
            .orElseThrow(() -> new SecurityForbiddenException(FORBIDDEN_MESSAGE));

    if (!passwordService.verify(request.getPassword(), user.getSalt(), user.getPassword())) {
      throw new SecurityForbiddenException(FORBIDDEN_MESSAGE);
    }

    user.setLastLogin(LocalDateTime.now());
    user.setToken(jwtUtil.generate(email));
    user.setActive(true);

    userRepository.save(user);

    return MAPPER.convertValue(user, LoginResponse.class);
  }
}
