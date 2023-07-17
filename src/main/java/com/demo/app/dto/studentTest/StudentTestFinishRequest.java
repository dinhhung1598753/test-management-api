package com.demo.app.dto.studentTest;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentTestFinishRequest {

    private Integer examClassId;

    private Integer testNo;

    private List<QuestionFinishRequest> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class QuestionFinishRequest {

        private Integer questionNo;

        private String selectedAnswerNo;

    }

}
