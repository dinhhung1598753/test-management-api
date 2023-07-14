package com.demo.app.dto.subject;

import com.demo.app.dto.chapter.ChapterRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectChaptersRequest {

    @NotBlank(message = "Please enter subject's title!")
    private String title;

    @NotBlank(message = "Please enter subject's code!")
    private String code;

    private String description;

    @Range(min = 2, max = 6, message = "Credit must in range form 2 to 6!")
    private Integer credit;

    private List<ChapterRequest> chapters;

}
