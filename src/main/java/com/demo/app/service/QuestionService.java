package com.demo.app.service;


import com.demo.app.dto.question.QuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface QuestionService {


    void addQuestion(QuestionRequest request);

    @Transactional
    List<QuestionResponse> getAllQuestionsBySubjectCode(String code);

    void updateQuestion(int questionId, QuestionRequest request);

    @Transactional
    void disableQuestion(int questionId);
}
