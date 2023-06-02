package com.demo.app.dto.chapter;

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

    @NotBlank(message = "Please enter order !")
    private int order;

}
