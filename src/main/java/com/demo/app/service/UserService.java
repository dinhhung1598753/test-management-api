package com.demo.app.service;

import com.demo.app.dto.user.UserResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    List<UserResponse> getUsers();

    Object getUserProfile(Authentication auth);
}
