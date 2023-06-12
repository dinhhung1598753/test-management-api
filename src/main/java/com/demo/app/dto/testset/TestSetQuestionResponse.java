package com.demo.app.dto.testset;

import com.demo.app.dto.question.QuestionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSetQuestionResponse {

    private int id;

    private int questionNo;

    private QuestionResponse question;

    @JsonProperty("answers")
    private List<TestSetQuestionAnswerResponse> answers;

}
