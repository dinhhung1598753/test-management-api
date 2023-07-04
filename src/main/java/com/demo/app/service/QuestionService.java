package com.demo.app.service;


import com.demo.app.dto.question.MultipleQuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.dto.question.SingleQuestionRequest;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {

    void saveQuestion(SingleQuestionRequest request, MultipartFile file) throws IOException;

    @Transactional
    void saveAllQuestions(MultipleQuestionRequest request);

    void importQuestion(MultipartFile file) throws IOException;

    @Transactional
    List<QuestionResponse> getAllQuestionsBySubjectCode(String code);

    void updateQuestion(int questionId, SingleQuestionRequest request, MultipartFile file) throws IOException;

    @Transactional
    void disableQuestion(int questionId);
}
