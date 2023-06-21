package com.demo.app.service;


import com.demo.app.dto.question.MultipleQuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.dto.question.SingleQuestionRequest;
import jakarta.transaction.Transactional;

import java.util.List;

public interface QuestionService {

    void saveQuestion(SingleQuestionRequest request);

    @Transactional
    void saveAllQuestions(MultipleQuestionRequest request);

    @Transactional
    List<QuestionResponse> getAllQuestionsBySubjectCode(String code);

    void updateQuestion(int questionId, SingleQuestionRequest request);

    @Transactional
    void disableQuestion(int questionId);
}
