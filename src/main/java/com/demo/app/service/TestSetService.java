package com.demo.app.service;

import com.demo.app.dto.testset.TestSetDetailResponse;
import jakarta.transaction.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface TestSetService {

    @Transactional
    List<Integer> createTestSetFromTest(int testId, Integer testSetQuantity) throws InterruptedException;

    TestSetDetailResponse getTestSetDetail(Integer testId, Integer testNo);

    ByteArrayInputStream exportTestSetToWord(Integer testId, Integer testNo) throws IOException;
}
