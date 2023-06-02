package com.demo.app.repository;

import com.demo.app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(Role.RoleType roleName);

    List<Role> findAllByRoleNameIn(List<Role.RoleType> roleTypes);

}
