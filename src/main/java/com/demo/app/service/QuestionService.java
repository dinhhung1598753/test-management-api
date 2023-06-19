package com.demo.app.service;


import com.demo.app.dto.question.SingleQuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

public interface QuestionService {


    void addQuestion(SingleQuestionRequest request);

    @Transactional
    void addAllQuestions(List<SingleQuestionRequest> requests) throws EntityNotFoundException;

    @Transactional
    List<QuestionResponse> getAllQuestionsBySubjectCode(String code);

    void updateQuestion(int questionId, SingleQuestionRequest request);

    @Transactional
    void disableQuestion(int questionId);
}
