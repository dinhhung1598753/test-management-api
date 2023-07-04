package com.demo.app.dto.student_test;

import com.demo.app.dto.testset.TestSetResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestResponse {

    @JsonIgnoreProperties({"createdAt", "updatedAt", "questionQuantity", "id"})
    private TestSetResponse testSet;

    @JsonProperty("questions")
    private List<StudentTestQuestionResponse> questions;



}
