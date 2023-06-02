package com.demo.app.dto.subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import jakarta.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectRequest {

    @NotBlank(message = "Please enter subject's title!")
    private String title;

    @NotBlank(message = "Please enter subject's code!")
    private String code;

    private String description;

    @Range(min = 2, max = 6, message = "Credit must in range form 2 to 6!")
    private int credit;
}
