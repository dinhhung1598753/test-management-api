package com.demo.app.dto.question;

import com.demo.app.dto.answer.AnswerRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultipleQuestionRequest {

    @NotBlank(message = "Please enter subject's code !")
    private String subjectCode;

    @Min(value = 1, message = "Chapter number must be greater than 0 !")
    private int chapterNo;

    private List<QuestionRequest> questions;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionRequest{

        private String topicText;

        private String topicImage;

        private String level;

        @JsonProperty("answers")
        private List<AnswerRequest> answers;
    }
}
