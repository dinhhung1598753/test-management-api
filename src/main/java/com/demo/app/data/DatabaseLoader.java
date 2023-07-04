package com.demo.app.data;

import com.demo.app.config.security.PasswordEncoder;
import com.demo.app.model.Role;
import com.demo.app.model.User;
import com.demo.app.repository.RoleRepository;
import com.demo.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeRoles() {
        if (roleRepository.count() != 0) return;
        List<Role> roles = new ArrayList<>();
        for (Role.RoleType type : Role.RoleType.values()) {
            roles.add(new Role(type));
        }
        roleRepository.saveAll(roles);

    }
 
    private void initializeAdminUser(){
        if (!userRepository.existsByUsername("admin")) {
            var roles = roleRepository.findAll();
            User user = User.builder()
                    .username("admin")
                    .email("knkuro00@gmail.com")
                    .password(passwordEncoder.passwordEncode().encode("admin"))
                    .roles(roles)
                    .build();
            userRepository.save(user);
        }

    }
}
