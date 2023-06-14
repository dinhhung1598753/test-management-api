package com.demo.app.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateRequest {

    @Email(regexp = "[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$", message = "Email is invalid !")
    private String email;

    @NotBlank(message = "Please enter your name !")
    private String fullName;

    @Min(value = 1, message = "course must be greater than 0 !")
    private int course;

    @NotBlank(message = "Please choose your birthday !")
    private String birthday;

    @NotBlank(message = "Please choose your Gender !")
    private String gender;

    @Pattern(regexp = "(84|0[3|5789])+([0-9]{8})\\b", message = "Phone number is invalid")
    private String phoneNumber;

    @NotBlank(message = "Please enter code !")
    @Length(max = 8)
    private String code;

}
