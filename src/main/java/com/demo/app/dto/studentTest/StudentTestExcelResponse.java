package com.demo.app.dto.studentTest;

import com.demo.app.marker.Excelable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestExcelResponse implements Excelable {

    private String classCode;

    private String testDate;

    private Double grade;

    private String state;

    private Integer course;

    private String email;

    private String fullName;

    private String studentCode;

}
