package com.demo.app.dto.student;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchRequest {

    @NotBlank(message = "Please enter field !")
    private String field;

    @NotBlank(message = "Please enter operator !")
    private String operator;

    @NotBlank(message = "Please enter value !")
    private String value;

}
