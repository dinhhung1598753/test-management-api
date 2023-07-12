package com.demo.app.dto.studentTest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestRequest {

    private String testDate;

    private int studentId;

    private String classCode;

    private int testNo;

}
