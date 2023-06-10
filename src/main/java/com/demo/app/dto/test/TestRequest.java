package com.demo.app.dto.test;

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
public class TestRequest {

    @NotBlank(message = "Please enter subject's code !")
    private String subjectCode;

    private List<Integer> chapterOrders;

    @Min(value = 1, message = "Question quantity must be greater than 1 !")
    @Max(value = 60, message = "Question quantity must be fewer than 60 !")
    private int questionQuantity;

    @NotBlank(message = "Please enter test day !")
    private String testDay;

    @Min(value = 1, message = "Duration quantity must be greater than 1 !")
    @Max(value = 60, message = "Duration quantity must be fewer than 60 !")
    private int duration;

}
