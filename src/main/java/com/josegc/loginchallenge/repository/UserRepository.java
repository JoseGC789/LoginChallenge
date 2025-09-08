package com.josegc.loginchallenge.repository;

import com.josegc.loginchallenge.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<User> findByEmailAndTokenAndIsActiveTrue(String email, String token);

  boolean existsByEmail(String email);
}
