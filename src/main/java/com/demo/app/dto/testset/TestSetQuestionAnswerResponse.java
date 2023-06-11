package com.demo.app.dto.testset;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSetQuestionAnswerResponse {

    private int id;

    private int answerNo;

    private String content;

}
