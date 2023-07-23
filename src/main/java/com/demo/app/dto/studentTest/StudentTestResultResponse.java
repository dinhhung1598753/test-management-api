package com.demo.app.dto.studentTest;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestResultResponse {

    private Integer testNo;

    private List<StudentTestDetailResponse.StudentTestQuestion> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentTestQuestion {

        private int questionNo;

        private String topicText;

        private String topicImage;

        private boolean isCorrected;

        private List<StudentTestDetailResponse.StudentTestQuestion.StudentTestAnswer> answers;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class StudentTestAnswer {

            private String answerNo;

            private String content;

            private boolean isSelected;

        }
    }

}
