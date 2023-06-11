package com.demo.app.dto.examClass;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClassRequest {

    @NotBlank(message = "Please enter Room Name !")
    private String roomName;

    @NotBlank(message = "Please enter Semester !")
    private String semester;

    @NotBlank(message = "Please enter Class's code !")
    private String code;

    private List<Integer> studentIds;

    private int testId;



}
