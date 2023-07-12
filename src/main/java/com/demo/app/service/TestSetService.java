package com.demo.app.service;

import com.demo.app.dto.testset.TestSetDetailResponse;
import jakarta.transaction.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface TestSetService {

    @Transactional
    void createTestSetFromTest(int testId, Integer testSetQuantity) throws InterruptedException;

    TestSetDetailResponse getTestSetDetail(Integer testId, Integer testNo);

    ByteArrayInputStream exportTestSetToWord(Integer testId, Integer testNo) throws IOException;
}
