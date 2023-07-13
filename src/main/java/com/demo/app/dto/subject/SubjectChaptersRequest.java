package com.demo.app.dto.subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectChaptersRequest {

    private String title;

    private String code;

    private String description;

    private Integer credit;

    private List<ChapterRequest> chapters;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterRequest{

        private String content;

        private int order;

    }
}
