package com.demo.app.service;

import com.demo.app.dto.studentTest.StudentTestDetailResponse;
import com.demo.app.dto.studentTest.StudentTestFinishRequest;

import java.io.IOException;
import java.security.Principal;

public interface StudentTestService {

    StudentTestDetailResponse attemptTest(String classCode, Principal principal);

    void finishStudentTest(StudentTestFinishRequest request, Principal principal) throws InterruptedException;

    void autoMarkingStudentTest(String classCode) throws IOException;
}
