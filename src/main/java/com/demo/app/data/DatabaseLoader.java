package com.demo.app.data;

import com.demo.app.config.security.PasswordEncoder;
import com.demo.app.model.Role;
import com.demo.app.model.User;
import com.demo.app.repository.RoleRepository;
import com.demo.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.account.username}")
    private String adminUsername;

    @Value("${app.admin.account.password}")
    private String adminPassword;

    @Value("${app.admin.account.email}")
    private String adminEmail;

    @Override
    @Transactional
    public void run(String... args) {
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeRoles() {
        if (roleRepository.count() != 0)
            return;
        var roles = Arrays.stream(Role.RoleType.values())
                .map(type -> Role.builder()
                        .roleName(type)
                        .build())
                .collect(Collectors.toList());
        roleRepository.saveAll(roles);
    }

    private void initializeAdminUser() {
        if (userRepository.existsByUsername(adminUsername))
            return;
        var roles = Collections.singletonList(roleRepository.findByRoleName(Role.RoleType.ROLE_ADMIN).get());
        var passwordEncoded = passwordEncoder.passwordEncode()
                .encode(adminPassword);
        User user = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoded)
                .roles(roles)
                .build();
        userRepository.save(user);
    }

}
