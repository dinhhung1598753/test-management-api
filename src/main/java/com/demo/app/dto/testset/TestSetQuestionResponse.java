package com.demo.app.dto.testset;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSetQuestionResponse {

    private int id;

    private int questionNo;

    private String level;

    private String topicText;

    private String topicImage;

    @JsonProperty("answers")
    private List<TestSetQuestionAnswerResponse> answers;

}
