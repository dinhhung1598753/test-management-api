package com.demo.app.service;

import com.demo.app.dto.user.UserResponse;

import java.util.List;

public interface UserService {


    List<UserResponse> getUsers();
}
