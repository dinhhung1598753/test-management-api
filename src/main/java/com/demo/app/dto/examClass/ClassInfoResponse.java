package com.demo.app.dto.examClass;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassInfoResponse {

    private TestResponse test;

    private ClassResponse examClass;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestResponse{

        private String subjectTitle;

        private String subjectCode;

        private String testDay;

        private String testTime;

        private int duration;

        private String state;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassResponse{

        private String roomName;

        private String semester;

        private String code;

        private Integer studentTestId;

    }


}
