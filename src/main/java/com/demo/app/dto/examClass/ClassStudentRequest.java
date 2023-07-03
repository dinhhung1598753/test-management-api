package com.demo.app.dto.examClass;

import com.demo.app.marker.Excelable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClassStudentRequest implements Excelable {

    private String code;

}
