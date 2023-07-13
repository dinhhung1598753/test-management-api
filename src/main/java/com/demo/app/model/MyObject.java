package com.demo.app.model;


import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyObject {
    private String class_code;
    private String student_code;
    private String exam_code;
    private List<ListAnswerObj> list_answer;

}



