package com.demo.app.dto.testset;

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

    private String topicText;

    private String topicImage;

    private String level;

    @JsonProperty("answers")
    private List<TestSetQuestionAnswerResponse> answers;

}
