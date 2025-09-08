package com.josegc.loginchallenge;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.josegc.loginchallenge.model.entity.Role;
import com.josegc.loginchallenge.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MainTest {
  @InjectMocks private Main main;
  @Mock private RoleRepository repository;

  @Test
  void testShouldAddDefaultRoles() throws Exception {
    when(repository.existsByName(Role.USER.getName())).thenReturn(false);
    when(repository.existsByName(Role.ADMIN.getName())).thenReturn(false);
    main.run(null);
    verify(repository).save(Role.USER);
    verify(repository).save(Role.ADMIN);
  }

  @Test
  void testAvoidAddingDefaultRolesIfExisting() throws Exception {
    when(repository.existsByName(Role.USER.getName())).thenReturn(true);
    when(repository.existsByName(Role.ADMIN.getName())).thenReturn(true);
    main.run(null);
    verify(repository, times(0)).save(Role.USER);
    verify(repository, times(0)).save(Role.ADMIN);
  }
}
