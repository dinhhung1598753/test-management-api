package com.demo.app.util.excel;

import com.demo.app.model.Student;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcelUtils {

    private static final DataFormatter formatter = new DataFormatter();
    private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final int FIRST_SHEET = 0;
    private static final int COLUMN_INDEX_USERNAME = 0;
    private static final int COLUMN_INDEX_EMAIL = 1;
    private static final int COLUMN_INDEX_FULLNAME = 3;
    private static final int COLUMN_INDEX_BIRTHDAY = 4;
    private static final int COLUMN_INDEX_GENDER = 5;
    private static final int COLUMN_INDEX_PHONE_NUMBER = 6;
    private static final int COLUMN_INDEX_CODE = 7;
    private static final int COLUMN_INDEX_ENABLED = 8;
    private static final int COLUMN_INDEX_COURSE = 9;
    private static final String[] HEADERs = {"Username", "Email", "Password", "Fullname", "Birthday", "Gender", "Phone Number", "Code", "Enabled", "Course"};

    public static boolean hasExcelFormat(MultipartFile file) {
        return Objects.equals(file.getContentType(), TYPE);
    }

    /**
     * A new method to read any Excel file
     */
    public static <T> List<T> convertExcelToDataTransferObject(MultipartFile file, Class<T> classType) throws IOException{
        var mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        var contents = getExcelContents(file);
        var jsons = convertContentsToJson(contents);
        return jsons.stream().map(json -> {
            try {
                return mapper.readValue(json, classType);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    private static List<Map<String, String>> getExcelContents(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            var evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            var sheet = workbook.getSheetAt(FIRST_SHEET);
            var rowStreamSupplier = getRowStreamSupplier(sheet);
            var headerRow = rowStreamSupplier.get()
                    .findFirst().get();
            var headerCells = getStream(headerRow)
                    .map(Cell::getStringCellValue)
                    .toList();
            var colNums = headerCells.size();
            return rowStreamSupplier.get().skip(1)
                    .map(row -> {
                        var cells = getStream(row)
                                .map(cell -> formatter.formatCellValue(cell, evaluator))
                                .toList();
                        return cellIteratorSupplier(colNums)
                                .get()
                                .collect(Collectors.toMap(headerCells::get, cells::get));
                    }).collect(Collectors.toList());
        }
    }

    private static List<String> convertContentsToJson(List<Map<String, String>> contents) {
        return contents.parallelStream()
                .map(content ->{
                    var joiner = new StringJoiner("").add("{");
                    var columnNums = content.size();
                    for(var entry : content.entrySet()) {
                        joiner.add("\"").add(entry.getKey()).add("\"")
                                .add(":")
                                .add("\"").add(entry.getValue()).add("\"");
                        if (--columnNums > 0)
                            joiner.add(",");
                    }
                    return joiner.add("}").toString();
                }).toList();
    }

    private static Supplier<Stream<Row>> getRowStreamSupplier(Iterable<Row> rows) {
        return () -> getStream(rows);
    }

    private static <T> Stream<T> getStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), true);
    }

    private static Supplier<Stream<Integer>> cellIteratorSupplier(int end) {
        return () -> numberStream(end);
    }

    private static Stream<Integer> numberStream(int end) {
        return IntStream.range(0, end).boxed();
    }


    public static ByteArrayInputStream studentsToExcelFile(List<Student> students) throws IOException {
        try (var outputStream = new ByteArrayOutputStream();
             var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Students");
            var headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERs.length; col++) {
                var cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            var rowIndex = 1;
            for (var student : students) {
                var row = sheet.createRow(rowIndex++);
                row.createCell(COLUMN_INDEX_USERNAME).setCellValue(student.getUser().getUsername());
                row.createCell(COLUMN_INDEX_EMAIL).setCellValue(student.getUser().getEmail());
                row.createCell(COLUMN_INDEX_FULLNAME).setCellValue(student.getFullname());
                row.createCell(COLUMN_INDEX_BIRTHDAY).setCellValue(student.getBirthday());
                row.createCell(COLUMN_INDEX_GENDER).setCellValue(student.getGender().toString());
                row.createCell(COLUMN_INDEX_PHONE_NUMBER).setCellValue(student.getPhoneNumber());
                row.createCell(COLUMN_INDEX_CODE).setCellValue(student.getCode());
                row.createCell(COLUMN_INDEX_ENABLED).setCellValue(student.getUser().isEnabled());
                row.createCell(COLUMN_INDEX_COURSE).setCellValue(student.getCourse());
            }
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

}