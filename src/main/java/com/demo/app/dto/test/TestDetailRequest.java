package com.demo.app.dto.test;

import com.demo.app.dto.question.QuestionResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestDetailRequest {

    @NotBlank(message = "Not empty!")
    private String testDay;

    private List<QuestionResponse> questionResponses;

    @Min(value = 1, message = "Question quantity must be greater than 1!")
    private int questionQuantity;

    private int duration;

    private String testTime;

    @Min(value = 1,message = "Total point must be greater than 0")
    @Max(value = 10, message = "Total point must be less than 10")
    private Integer totalPoint;
}
