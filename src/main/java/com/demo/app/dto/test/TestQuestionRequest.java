package com.demo.app.dto.test;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestionRequest {

    @NotBlank(message = "Please enter test day !")
    private String testDay;

    @NotBlank(message = "Please enter test time !")
    private String testTime;

    @NotNull(message = "Please choose questions to add to test !")
    private List<Integer> questionIds;

    @Min(value = 1, message = "Duration must be greater than 0 !")
    private int duration;

    @Min(value = 1,message = "Total point must be greater than 0")
    @Max(value = 100, message = "Total point must be less than 100")
    private Integer totalPoint;

}
