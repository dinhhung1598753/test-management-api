package com.demo.app.dto.student_test;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTestDetailResponse {

    private int testNo;

    private List<StudentTestQuestion> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentTestQuestion {

        private int questionNo;

        private String topicText;

        private String level;

        private String topicImage;

        private List<StudentTestAnswer> answers;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class StudentTestAnswer {

            private int answerNo;

            private String content;

            private String isSelected;
        }
    }

}
