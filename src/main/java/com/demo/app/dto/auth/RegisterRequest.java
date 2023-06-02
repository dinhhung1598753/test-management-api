package com.demo.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Please enter username !")
    private String username;

    @NotBlank(message = "Please enter email !")
    @Email(regexp = "[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$", message = "Email is invalid !")
    private String email;

    @NotBlank(message = "Please enter password !")
    private String password;

}
