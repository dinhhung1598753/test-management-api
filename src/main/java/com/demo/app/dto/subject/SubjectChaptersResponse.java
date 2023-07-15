package com.demo.app.dto.subject;

import com.demo.app.dto.chapter.ChapterResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectChaptersResponse {

    private int id;

    private String title;

    private String code;

    private String description;

    private int credit;

    private long chapterQuantity;

    private long questionQuantity;

    private List<ChapterResponse> chapters;

}
