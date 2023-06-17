package com.demo.app.dto.question;

import com.demo.app.dto.answer.AnswerResponse;
import com.demo.app.dto.chapter.ChapterResponse;
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

    private String topicImage;

    private String level;

    private String createdAt;

    private ChapterResponse chapter;

    private String subjectTitle;

    private String subjectCode;

    private List<AnswerResponse> answers = new ArrayList<>();
}
