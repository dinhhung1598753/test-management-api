package com.demo.app.service;

import com.demo.app.dto.auth.AuthenticationRequest;
import com.demo.app.dto.auth.AuthenticationResponse;
import com.demo.app.dto.auth.RegisterRequest;
import com.demo.app.exception.InvalidVerificationTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.io.IOException;

public interface AuthService {
    @Transactional
    AuthenticationResponse register(RegisterRequest registerRequest, HttpServletRequest request);

    @Transactional
    void activateUserAccount(String verifyToken) throws InvalidVerificationTokenException;

    AuthenticationResponse login(AuthenticationRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
