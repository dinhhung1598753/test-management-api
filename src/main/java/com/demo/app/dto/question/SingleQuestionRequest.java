package com.demo.app.dto.question;

import com.demo.app.dto.answer.AnswerRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleQuestionRequest {

    private int chapterId;

    @NotBlank(message = "Please enter question's topic !")
    private String topicText;

    @NotBlank(message = "Please enter question's level !")
    private String level;

    private String topicImage;

    @JsonProperty("answers")
    private List<AnswerRequest> answers;

}
