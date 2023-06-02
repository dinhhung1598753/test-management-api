package com.demo.app.dto.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {

    @NotBlank(message = "Please enter question's topic !")
    private String topicText;

    private MultipartFile topicImageFile;

    @NotBlank(message = "Please enter question's level !")
    private String level;

}
