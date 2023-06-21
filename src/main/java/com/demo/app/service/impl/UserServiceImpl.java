package com.demo.app.service.impl;

import com.demo.app.dto.user.UserResponse;
import com.demo.app.model.User;
import com.demo.app.repository.UserRepository;
import com.demo.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.parallelStream()
                .map(user -> {
                    var response = mapper.map(user, UserResponse.class);
                    var roles = user.getRoles().parallelStream()
                            .map(role -> role.getRoleName().name())
                            .collect(Collectors.toList());
                    response.setRoles(roles);
                    return response;
                }).collect(Collectors.toList());
    }
}
