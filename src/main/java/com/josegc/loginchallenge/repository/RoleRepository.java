package com.josegc.loginchallenge.repository;

import com.josegc.loginchallenge.model.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
  boolean existsByName(String name);
}
