package com.demo.app.dto.chapter;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterResponse {

    private int id;

    private String title;

    private String order;

}
