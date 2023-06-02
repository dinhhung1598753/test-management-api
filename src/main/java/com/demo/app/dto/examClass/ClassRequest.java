package com.demo.app.dto.examClass;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequest {

    @NotBlank(message = "Please enter Room Name !")
    private String roomName;

    @NotBlank(message = "Please enter Semester !")
    private String semester;

    @NotBlank(message = "Please enter Class's code !")
    private String code;

}
