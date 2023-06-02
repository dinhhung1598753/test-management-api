package com.demo.app.dto.examClass;

import com.demo.app.dto.student.StudentResponse;
import com.demo.app.dto.subject.SubjectResponse;
import com.demo.app.dto.teacher.TeacherResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassResponse {

    private int id;

    private String roomName;

    private String semester;

    private String code;

    private String createdDate;

    private TeacherResponse teacherResponse;

    private List<StudentResponse> studentResponses;

    private SubjectResponse subjectResponse;

}
