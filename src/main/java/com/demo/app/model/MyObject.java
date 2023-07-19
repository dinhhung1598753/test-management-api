package com.demo.app.model;


import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyObject {
    private String classCode;
    private String studentCode;
    private String examCode;
    private List<ListAnswerObj> questions;

}



