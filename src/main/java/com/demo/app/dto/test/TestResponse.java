package com.demo.app.dto.test;

import lombok.*;

import java.util.List;

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

    private String testDay;

    private String testTime;

    private int duration;

    private int questionQuantity;

    private String subjectCode;

    private String subjectTitle;

    private List<Integer> testSetNos;

}
