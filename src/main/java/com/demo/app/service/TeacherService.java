package com.demo.app.service;

import com.demo.app.dto.teacher.TeacherRequest;
import com.demo.app.dto.teacher.TeacherResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;

import java.util.List;

public interface TeacherService {
    void saveTeacher(TeacherRequest request) throws FieldExistedException;

    List<TeacherResponse> getAllTeacher();

    void updateTeacher(int teacherId, TeacherRequest request) throws EntityNotFoundException, FieldExistedException;

    void disableTeacher(int teacherId) throws EntityNotFoundException;

    void deleteTeacher(int teacherId) throws EntityNotFoundException;
}
