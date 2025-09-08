package com.josegc.loginchallenge.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@SuppressWarnings("PMD.")
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  @SuppressWarnings("java:S4502")
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity = httpSecurity.csrf().disable();

    httpSecurity =
        httpSecurity
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and();

    httpSecurity
        .authorizeRequests()
        .antMatchers("/h2-console/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/api/user/sign-up")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/api/user/login")
        .permitAll()
        .anyRequest()
        .authenticated();

    return httpSecurity.build();
  }

}
