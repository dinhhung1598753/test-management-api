package com.demo.app.dto.question;

import com.demo.app.marker.Excelable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionExcelRequest implements Excelable {

    private String topicText;

    private String topicImage;

    private String level;

    private int chapterNo;

    private String subjectCode;

    private String answer1;

    private String answer2;

    private String answer3;

    private String answer4;

    private int correctedAnswer;

}
