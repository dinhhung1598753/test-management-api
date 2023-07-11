package com.demo.app.dto.student_test;

import com.demo.app.marker.Excelable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentClassResponse implements Excelable {

    private String fullName;

    private Integer course;

    private String birthday;

    private String gender;

    private String phoneNumber;

    private String email;

    private String code;
}
