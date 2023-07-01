package com.demo.app.util.word;

import com.demo.app.dto.testset.TestSetDetailResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class WordUtils {

    private static final Map<Integer, String> answerNoText = Map.of(
            1, "A",
            2, "B",
            3, "C",
            4, "D");

    public static ByteArrayInputStream convertContentToWord(TestSetDetailResponse content) throws IOException {
        try (var document = new XWPFDocument();
             var outputStream = new ByteArrayOutputStream()) {
            XWPFParagraph paragraph = document.createParagraph();
            content.getQuestions().forEach(question -> {
                XWPFRun run = paragraph.createRun();
                run.setFontSize(14);
                run.setText("Question " + question.getQuestionNo() + ": " + question.getTopicText());
                run.addBreak();
                question.getAnswers().forEach(answer -> {
                    run.setText(answerNoText.get(answer.getAnswerNo()) + "." + answer.getContent());
                    run.addBreak();
                });
            });

            document.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

}
