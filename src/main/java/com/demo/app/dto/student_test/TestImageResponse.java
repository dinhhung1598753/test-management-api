package com.demo.app.dto.student_test;

import com.demo.app.dto.message.ResponseMessage;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TestImageResponse {

    private ResponseMessage responseMessage;

    private String path;

    private List<Filename> imageFilenames;


}
