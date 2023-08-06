package com.demo.app.service;

import com.demo.app.dto.offline.OfflineExam;
import com.demo.app.dto.offline.OfflineExamRequest;
import com.demo.app.dto.offline.OfflineExamResponse;
import com.demo.app.dto.studentTest.StudentTestAttemptResponse;
import com.demo.app.dto.studentTest.StudentTestDetailResponse;
import com.demo.app.dto.studentTest.StudentTestFinishRequest;
import com.demo.app.dto.studentTest.StudentTestResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface StudentTestService {

    StudentTestAttemptResponse attemptTest(String classCode, Principal principal);

    void finishStudentTest(StudentTestFinishRequest request, Principal principal) throws InterruptedException;

    List<OfflineExam> autoReadStudentOfflineExam(String classCode) throws IOException, InterruptedException;

    OfflineExamResponse markStudentOfflineTest(OfflineExamRequest request);

    StudentTestDetailResponse getStudentTestDetail(Integer studentTestId);

    List<StudentTestResponse> getAllTestOfStudent(Principal principal);
}
