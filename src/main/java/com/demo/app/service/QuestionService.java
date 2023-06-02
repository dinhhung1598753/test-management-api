package com.demo.app.service;


import com.demo.app.dto.answer.AnswerRequest;
import com.demo.app.dto.question.QuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;

public interface QuestionService {


    void addQuestion(int chapterId, QuestionRequest request) throws IOException;

    void addQuestionAnswers(int questionId, List<AnswerRequest> requests);

    @Transactional
    List<QuestionResponse> getAllQuestionsBySubjectCode(String code);

    void updateQuestion(int questionId, QuestionRequest request);

    @Transactional
    void disableQuestion(int questionId);
}
