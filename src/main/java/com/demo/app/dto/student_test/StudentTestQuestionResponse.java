package com.demo.app.dto.student_test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestQuestionResponse {

    private int questionNo;

    private String level;

    private String topicText;

    private String topicImage;

    private List<StudentTestQuestionAnswerResponse> answers;

}
