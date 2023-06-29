package com.demo.app.util.word;

import com.demo.app.dto.testset.TestSetDetailResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WordUtils {

    public ByteArrayInputStream convertContentToWord(TestSetDetailResponse content) throws IOException {
        try (var document = new XWPFDocument();
             var outputStream = new ByteArrayOutputStream()) {

            XWPFParagraph title = document.createParagraph();



            document.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

}
