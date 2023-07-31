package com.demo.app.dto.examClass;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassDetailResponse {

    private List<StudentClassResponse> students;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentClassResponse{

        private String fullName;

        private String code;

        private String testDate;

        private String state;

        private Double grade;

        private Integer studentTestId;

        private Integer mark;

    }

}


