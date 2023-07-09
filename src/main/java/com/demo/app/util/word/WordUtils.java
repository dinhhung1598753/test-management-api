package com.demo.app.util.word;

import com.deepoove.poi.XWPFTemplate;
import com.demo.app.dto.testset.TestSetDetailResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class WordUtils {

    public static ByteArrayInputStream convertTestToWord(TestSetDetailResponse content) throws IOException {
        try (var document = new XWPFDocument();
             var outputStream = new ByteArrayOutputStream()){
            var testSet = content.getTestSet();
            XWPFTemplate.compile("word/TestSet_Template.docx").render(new HashMap<String, Object>() {{
                put("testSet", testSet);
                put("duration", testSet.getDuration());
                put("testNo", testSet.getTestNo());
                put("questions", content.getQuestions());
            }}).writeAndClose(outputStream);
            document.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }


}
