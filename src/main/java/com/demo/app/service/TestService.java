package com.demo.app.service;

import com.demo.app.dto.test.*;
import jakarta.transaction.Transactional;

import java.util.List;

public interface TestService {
    TestDetailResponse createTestRandomQuestion(TestRequest request);

    @Transactional
    void createTestByChooseQuestions(TestQuestionRequest request);

    List<TestResponse> getAllTests();

    TestDetailResponse getTestDetail(int testId);

    void disableTest(int testId);

    @Transactional
    void updateTest(int testId, TestDetailRequest request);
}
