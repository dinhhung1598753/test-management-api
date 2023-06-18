package com.demo.app.service.impl;

import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.dto.test.*;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.Question;
import com.demo.app.model.Test;
import com.demo.app.repository.QuestionRepository;
import com.demo.app.repository.SubjectRepository;
import com.demo.app.repository.TestRepository;
import com.demo.app.service.TestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final QuestionRepository questionRepository;

    private final SubjectRepository subjectRepository;

    private final TestRepository testRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public TestDetailResponse createTestRandomQuestion(TestRequest request) throws EntityNotFoundException {
        var subject = subjectRepository.findByCode(request.getSubjectCode())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Code: %s not found !", request.getSubjectCode()), HttpStatus.NOT_FOUND));
        final int FIRST_RESULTS = 0;
        var pageable = PageRequest.of(FIRST_RESULTS, request.getQuestionQuantity());
        var questions = questionRepository.findQuestionBySubjectChapterOrder(
                request.getSubjectCode(),
                request.getChapterOrders(),
                pageable
        );
        var questionResponses = questions.stream()
                .map(question -> {
                    var response = mapper.map(question, QuestionResponse.class);
                    response.setSubjectTitle(subject.getTitle());
                    response.setSubjectCode(subject.getCode());
                    return response;
                })
                .collect(Collectors.toList());
        var test = Test.builder()
                .testDay(LocalDate.parse(request.getTestDay(), FORMATTER))
                .questionQuantity(request.getQuestionQuantity())
                .duration(request.getDuration())
                .build();

        test.setQuestions(questions.stream().toList());
        test.setSubject(subject);
        testRepository.save(test);

        return TestDetailResponse.builder()
                .questionQuantity(request.getQuestionQuantity())
                .testDay(request.getTestDay())
                .subjectCode(subject.getCode())
                .subjectTitle(subject.getTitle())
                .questionResponses(questionResponses)
                .duration(request.getDuration())
                .build();
    }

    @Override
    @Transactional
    public void createTestByChooseQuestions(TestQuestionRequest request) {
        var questions = questionRepository.findAllById(request.getQuestionIds());
        if (questions.isEmpty()){
            throw new EntityNotFoundException("Not found any question to add to test !", HttpStatus.NOT_FOUND);
        }
        var subject =questions.get(0).getChapter().getSubject();
        var test = Test.builder()
                .testDay(LocalDate.parse(request.getTestDay(), FORMATTER))
                .questionQuantity(questions.size())
                .duration(request.getDuration())
                .build();
        test.setQuestions(questions);
        test.setSubject(subject);
        testRepository.save(test);
    }

    @Override
    @Transactional
    public List<TestResponse> getAllTests() {
        var tests = testRepository.findByEnabledIsTrue();
        return tests.stream()
                .map(test -> {
                    var testResponse = mapper.map(test, TestResponse.class);
                    var subject = test.getSubject();
                    testResponse.setSubjectCode(subject.getCode());
                    testResponse.setSubjectTitle(subject.getTitle());
                    return testResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TestDetailResponse getTestDetail(int testId) {
        var test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test with id : %d not found !", testId),
                        HttpStatus.NOT_FOUND));
        var subject = test.getSubject();
        System.out.println(subject.getId());
        var questionResponses = test.getQuestions()
                .stream()
                .map(question -> mapper.map(question, QuestionResponse.class))
                .collect(Collectors.toList());
        return TestDetailResponse.builder()
                .questionResponses(questionResponses)
                .questionQuantity(test.getQuestionQuantity())
                .subjectCode(subject.getCode())
                .subjectTitle(subject.getTitle())
                .testDay(test.getTestDay().toString())
                .duration(test.getDuration())
                .build();
    }

    @Override
    @Transactional
    public void updateTest(int testId, TestDetailRequest request) {
        var test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cannot find any chapter with id %d", testId),
                        HttpStatus.NOT_FOUND));
        var questions = request.getQuestionResponses()
                .stream()
                .map(questionResponse -> mapper.map(questionResponse, Question.class))
                .collect(Collectors.toList());
        test.setQuestions(questions);
        test.setTestDay(LocalDate.parse(request.getTestDay(), FORMATTER));
        test.setDuration(request.getDuration());
        testRepository.save(test);
    }

    @Override
    public void disableTest(int testId) {
        var test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cannot find any chapter with id %d", testId),
                        HttpStatus.NOT_FOUND));
        test.setEnabled(false);
        testRepository.save(test);
    }


}
