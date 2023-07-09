package com.demo.app.dto.testset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSetDetailResponse {

    @JsonIgnoreProperties({"createdAt", "updatedAt", "questionQuantity", "id"})
    private TestSetResponse testSet;

    @JsonProperty("questions")
    private List<TestSetQuestionResponse> questions;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestSetQuestionResponse {

        private int id;

        private int questionNo;

        private String level;

        private String topicText;

        private String topicImage;

        @JsonProperty("answers")
        private List<TestSetQuestionAnswerResponse> answers;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TestSetQuestionAnswerResponse {

            private int id;

            private String answerNo;

            private String content;

        }
    }
}
