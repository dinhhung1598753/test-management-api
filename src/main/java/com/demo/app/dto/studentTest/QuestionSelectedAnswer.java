package com.demo.app.dto.studentTest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QuestionSelectedAnswer {

    private int questionNo;

    private String selectedAnswer;

    private Boolean isCorrected;

}
