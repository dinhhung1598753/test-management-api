package com.demo.app.service.impl;

import com.demo.app.config.security.PasswordEncoder;
import com.demo.app.dto.auth.AuthenticationRequest;
import com.demo.app.dto.auth.AuthenticationResponse;
import com.demo.app.dto.auth.RegisterRequest;
import com.demo.app.event.RegisterCompleteEvent;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import com.demo.app.exception.InvalidVerificationTokenException;
import com.demo.app.model.Role;
import com.demo.app.model.Token;
import com.demo.app.model.User;
import com.demo.app.repository.RoleRepository;
import com.demo.app.repository.TokenRepository;
import com.demo.app.repository.UserRepository;
import com.demo.app.service.AuthService;
import com.demo.app.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager manager;

    private final ModelMapper mapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final ApplicationEventPublisher publisher;
    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest, HttpServletRequest request) {
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new FieldExistedException("Email already taken!", HttpStatus.CONFLICT);
        }
        if(userRepository.existsByUsername(registerRequest.getUsername())){
            throw new FieldExistedException("Username already taken!", HttpStatus.CONFLICT);
        }

        var roles = Collections.singletonList(roleRepository.findByRoleName(Role.RoleType.ROLE_USER).get());
        var user = mapper.map(registerRequest, User.class);
        user.setRoles(roles);
        user.setPassword(encode(registerRequest.getPassword()));
        user.setEnabled(false);
        var savedUser = userRepository.save(user);

        publisher.publishEvent(new RegisterCompleteEvent(savedUser, verificationEmailUrl(request)));

        var jwtToken = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.generateRefreshToken(user);
        saveUserToken(savedUser,jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
    private String verificationEmailUrl(HttpServletRequest request) {
        return "http://" +request.getServerName()+":"
                +request.getServerPort()+request.getContextPath();
    }


    @Override
    @Transactional
    public void activateUserAccount(String verifyToken) throws InvalidVerificationTokenException{
        var token = tokenRepository.findByTokenAndExpiredFalse(verifyToken).orElseThrow(
                () -> new EntityNotFoundException("Invalid token !", HttpStatus.FORBIDDEN)
        );
        token.getUser().setEnabled(true);
        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);
    }

    private String encode(String password) {
        return passwordEncoder.passwordEncode().encode(password);
    }

    @Override
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userRepository.findByUsernameAndEnabledIsTrue(request.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("Username not found !", HttpStatus.BAD_REQUEST));
        var jwtToken = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken){
        var token = Token.builder()
                .token(jwtToken)
                .type(Token.TokenType.BEARER)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user){
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        final String refreshToken = authHeader.substring(7);
        final String username = jwtUtils.extractUsername(refreshToken);
        if(username != null){
            var user = userRepository.findByUsername(username).orElseThrow(
                    () -> new EntityNotFoundException("User not found !", HttpStatus.BAD_REQUEST));
            if(jwtUtils.isTokenValid(refreshToken, user)){
                var accessToken = jwtUtils.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }

        }
    }
}
