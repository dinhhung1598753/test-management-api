package com.demo.app.service;

import com.demo.app.dto.student.StudentRequest;
import com.demo.app.dto.student.StudentResponse;
import com.demo.app.dto.student.StudentSearchRequest;
import com.demo.app.dto.student.StudentUpdateRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface StudentService {

    void saveStudent(StudentRequest request) throws FieldExistedException;

    ByteArrayInputStream exportStudentsToExcel() throws IOException, IllegalAccessException;

    void importStudentExcel(MultipartFile file) throws FieldExistedException, IOException;

    List<StudentResponse> getAllStudents();

    List<StudentResponse> searchByFilter(StudentSearchRequest request);

    void updateStudentById(int studentId, StudentUpdateRequest request) throws EntityNotFoundException;

    void updateStudentProfile(Principal principal, StudentUpdateRequest request) throws EntityNotFoundException, FieldExistedException;

    void disableStudent(int studentId) throws EntityNotFoundException;

}
