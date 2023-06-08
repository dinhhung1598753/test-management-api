package com.demo.app.dto.test;

import com.demo.app.dto.question.QuestionResponse;
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

}
