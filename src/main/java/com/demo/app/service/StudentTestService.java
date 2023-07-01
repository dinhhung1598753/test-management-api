package com.demo.app.service;

import com.demo.app.dto.student_test.StudentTestDetailResponse;

import java.io.IOException;
import java.security.Principal;

public interface StudentTestService {

    StudentTestDetailResponse attemptTest(String classCode, Principal principal);

    void markingOfflineAnswer() throws IOException;
}
