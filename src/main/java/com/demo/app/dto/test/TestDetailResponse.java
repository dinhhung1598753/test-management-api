package com.demo.app.dto.test;

import com.demo.app.dto.question.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestDetailResponse {

    private String subjectTitle;

    private String subjectCode;

    private int questionQuantity;

    private String testDay;

    private int duration;

    private List<QuestionResponse> questionResponses;

}
