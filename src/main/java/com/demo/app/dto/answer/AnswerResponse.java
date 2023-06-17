package com.demo.app.dto.answer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {

    private int id;

    private String content;

    @JsonProperty("isCorrected")
    private String isCorrected;

}
