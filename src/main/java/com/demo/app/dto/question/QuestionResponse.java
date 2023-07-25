package com.demo.app.dto.question;

import com.demo.app.dto.answer.AnswerResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {

    private int id;

    private String subjectCode;

    private String topicText;

    private String topicImage;

    private String level;

    private List<AnswerResponse> answers;
}
