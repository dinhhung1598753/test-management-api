package com.demo.app.dto.testset;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSetResponse {

    private int id;

    private int testNo;

    private String subjectTitle;

    private String subjectCode;

    private int questionQuantity;

    private String createdAt;

    private String updatedAt;

    private String testDay;

}
