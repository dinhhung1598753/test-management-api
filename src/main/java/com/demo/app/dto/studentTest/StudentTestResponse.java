package com.demo.app.dto.studentTest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StudentTestResponse {

    private Integer id;

    private String examClassCode;

    private Integer testNo;

    private String state;

    private String testDate;

    private Double grade;

    private Integer mark;

}
