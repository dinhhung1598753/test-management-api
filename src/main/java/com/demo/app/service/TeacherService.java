package com.demo.app.service;

import com.demo.app.dto.teacher.TeacherRequest;
import com.demo.app.dto.teacher.TeacherResponse;
import com.demo.app.dto.teacher.TeacherUpdateRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.DuplicatedUniqueValueException;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface TeacherService {
    void saveTeacher(TeacherRequest request) throws DuplicatedUniqueValueException;

    void importTeacherExcel(MultipartFile file) throws IOException;

    List<TeacherResponse> getAllTeacher();

    ByteArrayInputStream exportTeachersToExcel() throws IOException;

    void updateTeacherById(int teacherId, TeacherUpdateRequest request) throws EntityNotFoundException, DuplicatedUniqueValueException;

    @Transactional
    void updateTeacherProfile(Principal principal, TeacherUpdateRequest request)  throws EntityNotFoundException, DuplicatedUniqueValueException;

    void disableTeacher(int teacherId) throws EntityNotFoundException;

}
