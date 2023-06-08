package com.demo.app.model;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class MyObject {
    private String sbd;
    private String mdt;
    private List<ListAnswerObj> listAnswer;

    public MyObject(String sbd, String mdt, List<ListAnswerObj> listAnswer) {
        this.sbd = sbd;
        this.mdt = mdt;
        this.listAnswer = listAnswer;
    }
}



