package com.demo.app.service;

import com.demo.app.dto.examClass.ClassRequest;
import com.demo.app.dto.examClass.ClassResponse;
import jakarta.transaction.Transactional;

import java.security.Principal;
import java.util.List;

public interface ExamClassService {
    @Transactional
    void createExamClass(ClassRequest request, Principal principal);

    void addStudentsToExamClass(int examClassId, List<Integer> studentIds);

    void addTeacherToExamClass(int examClassId, int teacherId);

    void addSubjectToExamClass(int examClassId, int subjectId);

    List<ClassResponse> getAllEnabledExamClass();

    void disableExamClass(int examClassId);
}
