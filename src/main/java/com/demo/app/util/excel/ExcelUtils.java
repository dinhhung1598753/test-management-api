package com.demo.app.util.excel;

import com.demo.app.exception.FileInputException;
import com.demo.app.marker.Excelable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcelUtils {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final int FIRST_SHEET = 0;

    public static boolean hasExcelFormat(MultipartFile file) {
        return Objects.equals(file.getContentType(), TYPE);
    }

    /**
     * Read any object that can be excelable to Excel file
     */
    public static <T extends Excelable> List<T> convertExcelToDataTransferObject(MultipartFile file, Class<T> classType) throws IOException {
        var mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        var contents = getExcelContents(file);
        var jsons = convertContentsToJson(contents);
        return jsons.stream()
                .map(json -> {
                    try {
                        return mapper.readValue(json, classType);
                    } catch (JsonProcessingException e) {
                        throw new FileInputException("This excel not true with it template !", HttpStatus.CONFLICT);
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
                                .map(cell -> FORMATTER.formatCellValue(cell, evaluator))
                                .toList();
                        return cellIteratorSupplier(colNums)
                                .get()
                                .collect(Collectors.toMap(headerCells::get, cells::get));
                    }).collect(Collectors.toList());
        }
    }

    private static List<String> convertContentsToJson(List<Map<String, String>> contents) {
        return contents.parallelStream()
                .map(content -> {
                    var joiner = new StringJoiner("").add("{");
                    var columnNums = content.size();
                    for (var entry : content.entrySet()) {
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


    /**
     * Write any Excel file from object that can excelable
     */
    public static <T extends Excelable> ByteArrayInputStream convertContentsToExcel(List<T> objects) throws IOException {
        try (var outputStream = new ByteArrayOutputStream();
             var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet(objects.getClass().getName());
            var contents = convertObjectsToContents(objects);

            var title = contents.get(0).keySet().iterator();
            var size = contents.get(0).size();
            var rowTitle = sheet.createRow(0);
            int colNum, colRow = 0;
            var font = workbook.createFont();
            font.setBold(true);
            var style = workbook.createCellStyle();
            style.setFont(font);
            for (colNum = 0; colNum < size; colNum++){
                var cell = rowTitle.createCell(colNum);
                cell.setCellValue(title.next());
                cell.setCellStyle(style);
            }
            colNum = 0;
            for (var content : contents) {
                var row = sheet.createRow(++colRow);
                for (var entry : content.entrySet()) {
                    row.createCell(colNum++).setCellValue(entry.getValue());
                }
                colNum = 0;
            }

            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }

    }

    private static <T extends Excelable> List<Map<String, String>> convertObjectsToContents(List<T> objects) {
        return objects.stream()
                .map(object -> Arrays.stream(object.getClass().getDeclaredFields())
                        .parallel()
                        .peek(field -> field.setAccessible(true))
                        .collect(Collectors.toMap(Field::getName, field -> {
                            try {
                                return field.get(object).toString();
                            } catch (IllegalAccessException e) {
                                throw new FileInputException(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
                            }
                        }))
                ).collect(Collectors.toList());
    }

}