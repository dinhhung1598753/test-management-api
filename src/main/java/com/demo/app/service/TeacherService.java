package com.demo.app.service;

import com.demo.app.dto.teacher.TeacherRequest;
import com.demo.app.dto.teacher.TeacherResponse;
import com.demo.app.dto.teacher.TeacherUpdateRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface TeacherService {
    void saveTeacher(TeacherRequest request) throws FieldExistedException;

    void importTeacherExcel(MultipartFile file) throws IOException;

    List<TeacherResponse> getAllTeacher();

    ByteArrayInputStream exportTeachersToExcel() throws IOException;

    void updateTeacher(int teacherId, TeacherUpdateRequest request) throws EntityNotFoundException, FieldExistedException;

    void disableTeacher(int teacherId) throws EntityNotFoundException;

}
