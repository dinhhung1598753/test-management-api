package com.demo.app.dto.question;

import com.demo.app.dto.answer.AnswerResponse;
import com.demo.app.dto.chapter.ChapterResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {

    private int id;

    private String createdAt;

    private ChapterResponse chapter;

    private String subjectTitle;

    private String subjectCode;

    private String topicText;

    private String topicImage;

    private String level;

    private List<AnswerResponse> answers;
}
