package com.demo.app.dto.test;

import jakarta.validation.constraints.Min;
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

    private String testDay;

    @NotNull(message = "Please choose questions to add to test !")
    private List<Integer> questionIds;

    @Min(value = 1, message = "Duration must be greater than 0 !")
    private int duration;

}
