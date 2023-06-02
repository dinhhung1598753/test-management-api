package com.demo.app.service;

import com.demo.app.dto.student.StudentRequest;
import com.demo.app.dto.student.StudentResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import com.demo.app.exception.FileInputException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface StudentService {

    void saveStudentsExcelFile(MultipartFile file) throws FileInputException, FieldExistedException;

    ByteArrayInputStream exportStudentsExcel() throws FileInputException;

    void saveStudent(StudentRequest request) throws FieldExistedException;

    List<StudentResponse> getAllStudents();

    void updateStudent(int studentId, StudentRequest request) throws EntityNotFoundException;

    void disableStudent(int studentId) throws EntityNotFoundException;

}
