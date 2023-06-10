package com.demo.app.util;

import com.demo.app.model.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class ExcelUtils {

    private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final int COLUMN_INDEX_USERNAME = 0;
    private static final int COLUMN_INDEX_EMAIL = 1;
    private static final int COLUMN_INDEX_PASSWORD = 2;
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

    public static Map<User, Student> excelFileToUserStudents(MultipartFile file) throws IOException {
        try (var inputStream = file.getInputStream();
             var workbook = new XSSFWorkbook(inputStream)) {

            var sheet = workbook.getSheetAt(0);
            var userStudents = new HashMap<User, Student>();

            for (var row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                var user = new User();
                user.setEnabled(true);
                var student = new Student();
                row.forEach(cell -> {
                    switch (cell.getColumnIndex()) {
                        case COLUMN_INDEX_USERNAME -> user.setUsername(cell.getStringCellValue());
                        case COLUMN_INDEX_EMAIL -> user.setEmail(cell.getStringCellValue());
                        case COLUMN_INDEX_PASSWORD -> user.setPassword(cell.getStringCellValue());
                        case COLUMN_INDEX_FULLNAME -> student.setFullname(cell.getStringCellValue());
                        case COLUMN_INDEX_BIRTHDAY -> {
                            Date date = cell.getDateCellValue();
                            LocalDate birthday = Instant.ofEpochMilli(date.getTime())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            student.setBirthday(birthday);
                        }
                        case COLUMN_INDEX_GENDER -> student.setGender(Gender.valueOf(cell.getStringCellValue()));
                        case COLUMN_INDEX_PHONE_NUMBER -> student.setPhoneNumber(cell.getStringCellValue());
                        case COLUMN_INDEX_CODE -> student.setCode(String.valueOf(cell.getNumericCellValue()));
                        case COLUMN_INDEX_COURSE -> student.setCourse((int) cell.getNumericCellValue());
                    }
                    userStudents.put(user, student);
                });
            }
            return userStudents;
        }
    }

    public static ByteArrayInputStream studentsToExcelFile(List<Student> students) throws IOException {
        try( var outputStream = new ByteArrayOutputStream();
             var workbook = new XSSFWorkbook()){
            var sheet = workbook.createSheet("Students");
            var headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERs.length; col++){
                var cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            var rowIndex = 1;
            for(var student : students){
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

    public static Map<User, Teacher> excelFileToUserTeachers(MultipartFile file) throws IOException {
        try (var inputStream = file.getInputStream();
             var workbook = new XSSFWorkbook(inputStream)) {

            var sheet = workbook.getSheetAt(0);
            var userTeachers = new HashMap<User, Teacher>();

            for (var row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                var user = new User();
                user.setEnabled(true);
                var teacher = new Teacher();
                row.forEach(cell -> {
                    switch (cell.getColumnIndex()) {
                        case COLUMN_INDEX_USERNAME -> user.setUsername(cell.getStringCellValue());
                        case COLUMN_INDEX_EMAIL -> user.setEmail(cell.getStringCellValue());
                        case COLUMN_INDEX_PASSWORD -> user.setPassword(cell.getStringCellValue());
                        case COLUMN_INDEX_FULLNAME -> teacher.setFullname(cell.getStringCellValue());
                        case COLUMN_INDEX_BIRTHDAY -> {
                            Date date = cell.getDateCellValue();
                            LocalDate birthday = Instant.ofEpochMilli(date.getTime())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            teacher.setBirthday(birthday);
                        }
                        case COLUMN_INDEX_GENDER -> teacher.setGender(Gender.valueOf(cell.getStringCellValue()));
                        case COLUMN_INDEX_PHONE_NUMBER -> teacher.setPhoneNumber(cell.getStringCellValue());
                        case COLUMN_INDEX_CODE -> teacher.setCode(String.valueOf(cell.getNumericCellValue()));
                    }
                    userTeachers.put(user, teacher);
                });
            }
            return userTeachers;
        }
    }
    public static ByteArrayInputStream teachersToExcelFile(List<Teacher> teachers) throws IOException {
        try( var outputStream = new ByteArrayOutputStream();
             var workbook = new XSSFWorkbook()){
            var sheet = workbook.createSheet("Teachers");
            var headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERs.length; col++){
                var cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            var rowIndex = 1;
            for(var teacher : teachers){
                var row = sheet.createRow(rowIndex++);
                row.createCell(COLUMN_INDEX_USERNAME).setCellValue(teacher.getUser().getUsername());
                row.createCell(COLUMN_INDEX_EMAIL).setCellValue(teacher.getUser().getEmail());
                row.createCell(COLUMN_INDEX_FULLNAME).setCellValue(teacher.getFullname());
                row.createCell(COLUMN_INDEX_BIRTHDAY).setCellValue(teacher.getBirthday());
                row.createCell(COLUMN_INDEX_GENDER).setCellValue(teacher.getGender().toString());
                row.createCell(COLUMN_INDEX_PHONE_NUMBER).setCellValue(teacher.getPhoneNumber());
                row.createCell(COLUMN_INDEX_CODE).setCellValue(teacher.getCode());
                row.createCell(COLUMN_INDEX_ENABLED).setCellValue(teacher.getUser().isEnabled());
            }
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }
}