package com.demo.app.dto.studentTest;

import com.demo.app.dto.student.StudentResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StudentTestResponse {

    @JsonIgnoreProperties(
            {"id", "username", "phoneNumber",
            "birthday", "createdAt"})
    private StudentResponse student;

    private String testDate;

    private Double grade;

}
