package com.demo.app.dto.question;

import com.demo.app.dto.answer.AnswerResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {

    private int id;

    private String topicText;

    private byte[] topicImage;

    private String level;

    private String createdDate;

    private List<AnswerResponse> answers = new ArrayList<>();

}
