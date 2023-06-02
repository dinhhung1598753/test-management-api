package com.demo.app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AuthenticationRequest {

    @NotBlank(message = "Please enter username !")
    private String username;

    @NotBlank(message = "Please enter password !")
    private String password;

}
