package com.demo.app.service;

import com.demo.app.dto.offline.OfflineExam;
import com.demo.app.dto.offline.OfflineExamRequest;
import com.demo.app.dto.offline.OfflineExamResponse;
import com.demo.app.dto.studentTest.StudentTestDetailResponse;
import com.demo.app.dto.studentTest.StudentTestFinishRequest;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface StudentTestService {

    StudentTestDetailResponse attemptTest(String classCode, Principal principal);

    void finishStudentTest(StudentTestFinishRequest request, Principal principal) throws InterruptedException;

    List<OfflineExam> autoReadStudentOfflineExam(String classCode) throws IOException, InterruptedException;

    OfflineExamResponse markStudentOfflineTest(OfflineExamRequest request);
}
