package com.demo.app.service;


import com.demo.app.dto.question.QuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

public interface QuestionService {


    void addQuestion(QuestionRequest request);

    @Transactional
    void addAllQuestions(List<QuestionRequest> requests) throws EntityNotFoundException;

    @Transactional
    List<QuestionResponse> getAllQuestionsBySubjectCode(String code);

    void updateQuestion(int questionId, QuestionRequest request);

    @Transactional
    void disableQuestion(int questionId);
}
