package com.demo.app.dto.studentTest;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StudentTestDetailResponse {

    private int testNo;

    private Integer mark;

    private Double grade;

    private List<StudentTestQuestion> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class StudentTestQuestion {

        private int questionNo;

        private String topicText;

        private String topicImage;

        private Boolean isCorrected;

        private List<StudentTestAnswer> answers;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @ToString
        public static class StudentTestAnswer {

            private String answerNo;

            private String content;

            private Boolean isSelected;

        }
    }

}
