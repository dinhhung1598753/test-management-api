package com.demo.app.dto.auth;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class AuthenticationResponse {

    private String message;

    private String username;

    private String email;

    private List<String> roles;

    private String accessToken;

    private String refreshToken;


}
