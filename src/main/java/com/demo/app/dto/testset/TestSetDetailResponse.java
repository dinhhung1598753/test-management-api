package com.demo.app.dto.testset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSetDetailResponse {

    private int duration;

    @JsonProperty("questions")
    private List<TestSetQuestionResponse> questions;
}
