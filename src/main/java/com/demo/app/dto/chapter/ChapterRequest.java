package com.demo.app.dto.chapter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterRequest {

    @NotBlank(message = "Please enter title !")
    private String title;

    @Min(value = 1, message = "Chapter's order must be greater than 0")
    private int order;

}
