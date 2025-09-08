package com.josegc.loginchallenge.repository;

import com.josegc.loginchallenge.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
  Optional<User> findByEmailAndTokenAndIsActiveTrue(String email, String token);

  boolean existsByEmail(String email);
}
