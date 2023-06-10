package com.demo.app.dto.test;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TestResponse {

    private int id;

    private String createdAt;

    private String updatedAt;

    private double totalPoint;

    private String testDay;

    private String subjectCode;

    private String subjectTitle;

    private int duration;

}
