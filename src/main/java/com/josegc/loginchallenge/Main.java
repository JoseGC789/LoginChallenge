package com.josegc.loginchallenge;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josegc.loginchallenge.model.entity.Role;
import com.josegc.loginchallenge.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@AllArgsConstructor
@EnableTransactionManagement
public class Main implements ApplicationRunner {
  public static final ObjectMapper MAPPER =
      new ObjectMapper()
          .findAndRegisterModules()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private final RoleRepository repository;

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (!repository.existsByName(Role.USER.getName())) {
      repository.save(Role.USER);
    }

    if (!repository.existsByName(Role.ADMIN.getName())) {
      repository.save(Role.ADMIN);
    }
  }
}
