package com.josegc.loginchallenge.model.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "users")
public class User {

  @Id private UUID id;

  private String name;

  @Column(unique = true)
  @ToString.Exclude
  private String email;

  @ToString.Exclude private String password;

  @ToString.Exclude private String salt;

  private LocalDateTime created;

  private LocalDateTime lastLogin;

  private String token;

  private boolean isActive;

  @ManyToMany(targetEntity = Role.class)
  private Set<Role> roles;

  @OneToMany(targetEntity = Phone.class, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Phone> phones;
}
