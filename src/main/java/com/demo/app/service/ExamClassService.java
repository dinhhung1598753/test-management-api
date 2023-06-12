package com.demo.app.service;

import com.demo.app.dto.examClass.ClassRequest;
import com.demo.app.dto.examClass.ClassResponse;
import com.demo.app.model.ExamClass;
import jakarta.transaction.Transactional;

import java.security.Principal;
import java.util.List;

public interface ExamClassService {
    @Transactional
    void createExamClass(ClassRequest request, Principal principal);

    ExamClass joinExamClassByCode(String classCode, Principal principal);

    List<ClassResponse> getAllEnabledExamClass();

    void disableExamClass(int examClassId);
}
