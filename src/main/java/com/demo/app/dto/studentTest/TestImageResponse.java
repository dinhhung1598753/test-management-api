package com.demo.app.dto.studentTest;

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

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class Filename{
        private String filename;
    }

}
