package com.demo.app.service;

import com.demo.app.dto.examClass.ClassDetailResponse;
import com.demo.app.dto.examClass.ClassInfoResponse;
import com.demo.app.dto.examClass.ClassRequest;
import com.demo.app.dto.examClass.ClassResponse;
import com.demo.app.model.ExamClass;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface ExamClassService {
    @Transactional
    void createExamClass(ClassRequest request, Principal principal);

    ExamClass joinExamClassByCode(String classCode, Principal principal);

    @Transactional
    void addStudentToClass(String examClassCode, List<String> studentCodes);

    @Transactional
    void importClassStudents(String classCode, MultipartFile file) throws IOException;

    List<ClassResponse> getAllEnabledExamClass();

    ClassDetailResponse getExamClassDetail(int examClassId);

    List<ClassResponse> getStudentExamClass(Principal principal);

    ClassInfoResponse getExamClassInfo(Integer examClassId, Principal principal);

    ByteArrayInputStream exportStudentTestToExcel(String code) throws IOException;

    void disableExamClass(int examClassId);
}
