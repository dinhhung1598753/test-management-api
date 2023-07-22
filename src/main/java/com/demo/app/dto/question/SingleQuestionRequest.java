package com.demo.app.dto.question;

import com.demo.app.dto.answer.AnswerRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SingleQuestionRequest {

    private Integer chapterId;

    @NotBlank(message = "Please enter question's topic !")
    private String topicText;

    @NotBlank(message = "Please enter question's level !")
    private String level;

    @JsonProperty("answers")
    private List<AnswerRequest> answers;

}
