package com.demo.app.dto.teacher;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherUpdateRequest {

    @Email(regexp = "[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$", message = "Email is invalid !")
    private String email;

    @NotBlank(message = "Please enter your name !")
    private String fullName;

    @NotBlank(message = "Please enter your birthday !")
    private String birthday;

    @NotBlank(message = "Please enter your gender !")
    private String gender;

    @Pattern(regexp = "(84|0[3|5789])+([0-9]{8})\\b", message = "Phone number is invalid")
    private String phoneNumber;

    @NotBlank(message = "Please enter code !")
    @Length(max = 8)
    private String code;

}
