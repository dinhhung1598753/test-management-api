package com.demo.app.service;

import com.demo.app.dto.test.*;
import jakarta.transaction.Transactional;

import java.util.List;

public interface TestService {
    Integer createTestRandomQuestion(TestRequest request);

    @Transactional
    Integer createTestByChooseQuestions(TestQuestionRequest request);

    List<TestResponse> getAllTests();

    void disableTest(int testId);

    @Transactional
    void updateTest(int testId, TestDetailRequest request);
}
