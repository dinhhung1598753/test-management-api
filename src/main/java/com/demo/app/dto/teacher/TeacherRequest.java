package com.demo.app.dto.teacher;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRequest {

    @NotBlank(message = "Please enter username !")
    private String username;

    @Email(regexp = "[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$", message = "Email is invalid !")
    private String email;

    @NotBlank(message = "Please enter password !")
    private String password;

    @NotBlank(message = "Please enter your name !")
    private String fullName;

    private String birthday;

    private String gender;

    @Pattern(regexp = "(84|0[3|5789])+([0-9]{8})\\b", message = "Phone number is invalid")
    private String phoneNumber;
}
