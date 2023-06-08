package com.demo.app.model;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class ListAnswerObj {
    private Integer stt;
    private String answer;

    public ListAnswerObj(Integer stt, String answer) {
        this.stt = stt;
        this.answer = answer;
    }
}
