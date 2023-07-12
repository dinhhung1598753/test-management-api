package com.demo.app.dto.studentTest;

import com.demo.app.marker.Excelable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestExcelResponse implements Excelable {

    private String examClassCode;

    private String testDate;

    private Double grade;

    private String fullName;

    private String studentCode;

}
