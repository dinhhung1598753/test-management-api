package com.demo.app.util.excel;

import com.demo.app.exception.FileInputException;
import com.demo.app.marker.Excelable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcelUtils {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private static final String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final int FIRST_SHEET = 0;

    public static boolean notHaveExcelFormat(MultipartFile file) {
        return !Objects.equals(file.getContentType(), EXCEL_TYPE);
    }

    /**
     * Read any object that can be excelable to Excel file
     */
    public static <T extends Excelable> List<T> convertExcelToDataTransferObject(MultipartFile file, Class<T> classType) throws IOException {
        var mapper = new ObjectMapper();
        var contents = getExcelContents(file);
        var jsons = convertContentsToJson(contents);
        return jsons.stream()
                .map(json -> {
                    try {
                        return mapper.readValue(json, classType);
                    } catch (JsonProcessingException e) {
                        throw new FileInputException("This excel not right with it template !" + e.getMessage(), HttpStatus.CONFLICT);
                    }
                }).collect(Collectors.toList());
    }

    public static void readExternalExcelFile(MultipartFile file) throws IOException {
        var test = getExcelContents(file);
        test.forEach(l -> l.forEach((k, v) -> System.out.println(k + " - " + v)));
    }

    private static List<Map<String, String>> getExcelContents(MultipartFile file) throws IOException {
        try (var inputStream = file.getInputStream();
             var workbook = WorkbookFactory.create(inputStream)) {
            var evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            var sheet = workbook.getSheetAt(FIRST_SHEET);
            var rowStreamSupplier = getRowStreamSupplier(sheet);
            var headerRow = rowStreamSupplier.get()
                    .findFirst().get();
            var headerCells = getStream(headerRow)
                    .map(cell -> reverseTitleText(cell.getStringCellValue()))
                    .toList();
            var colNums = headerCells.size();
            return rowStreamSupplier.get()
                    .skip(1)
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

    private static String reverseTitleText(String title){
        title = StringUtils.uncapitalize(title);
        var joiner = new StringJoiner("");
        var letters = title.split("\\s+");
        for (String letter : letters) {
            joiner.add(letter);
        }
        return joiner.toString();
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
            createTitleRow(contents.get(0).keySet(), workbook, sheet);
            createBodyRow(contents, sheet);
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }

    }

    private static void createTitleRow(Set<String> header, Workbook workbook, Sheet sheet) {
        var titles = header.iterator();
        var size = header.size();
        var rowTitle = sheet.createRow(0);
        rowTitle.setHeight((short) 500);
        var font = workbook.createFont();
        font.setFontHeight((short) 300);
        font.setBold(true);
        var style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        for (var colNum = 0; colNum < size; colNum++) {
            var title = convertTitleText(titles.next());
            var cell = rowTitle.createCell(colNum);
            cell.setCellValue(title);
            cell.setCellStyle(style);
            sheet.setColumnWidth(colNum, (30-title.length()) * 275);
        }
    }

    private static void createBodyRow(List<Map<String, String>> contents, Sheet sheet) {
        int colNum = 0, colRow = 0;
        for (var content : contents) {
            var row = sheet.createRow(++colRow);
            for (var entry : content.entrySet()) {
                row.createCell(colNum++).setCellValue(entry.getValue());
            }
            colNum = 0;
        }
    }

    private static <T extends Excelable> List<Map<String, String>> convertObjectsToContents(List<T> objects) {
        return objects.parallelStream()
                .map(object -> Arrays.stream(object.getClass().getDeclaredFields())
                        .parallel()
                        .peek(field -> field.setAccessible(true))
                        .collect(Collectors.toMap(Field::getName, field -> {
                            try {
                                return field.get(object) == null ? "null" : field.get(object).toString();
                            } catch (IllegalAccessException e) {
                                throw new FileInputException(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
                            }
                        }))
                ).collect(Collectors.toList());
    }

    private static String convertTitleText(String title){
        title = StringUtils.capitalize(title);
        var joiner = new StringJoiner(" ");
        var letters = title.split("(?=\\p{Upper})");
        for (var letter : letters) {
            joiner.add(letter);
        }
        return joiner.toString();
    }

}