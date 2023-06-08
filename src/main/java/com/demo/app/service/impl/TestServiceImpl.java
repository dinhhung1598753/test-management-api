package com.demo.app.service.impl;

import com.demo.app.dto.answer.AnswerResponse;
import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.dto.test.TestDetailRequest;
import com.demo.app.dto.test.TestRequest;
import com.demo.app.dto.test.TestDetailResponse;
import com.demo.app.dto.test.TestResponse;
import com.demo.app.dto.testset.TestSetRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.*;
import com.demo.app.repository.*;
import com.demo.app.service.TestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final int TEST_NO_ROOT_NUMBER = 100;

    private final QuestionRepository questionRepository;

    private final SubjectRepository subjectRepository;

    private final TestRepository testRepository;

    private final TestSetRepository testSetRepository;

    private final TestSetQuestionRepository testSetQuestionRepository;

    private final TestSetQuestionAnswerRepository testSetQuestionAnswerRepository;

    private final ModelMapper mapper;

    @Override
    public TestDetailResponse createTestFirstStep(TestRequest request) throws EntityNotFoundException {
        var subject = subjectRepository.findByCode(request.getSubjectCode()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Code: %s not found !", request.getSubjectCode()), HttpStatus.NOT_FOUND)
        );
        final var FIRST_RESULTS = 0;
        var pageable = PageRequest.of(FIRST_RESULTS, request.getQuestionQuantity());
        var questions = questionRepository.findQuestionBySubjectChapterOrder(request.getSubjectCode(), request.getChapterOrders(), pageable);
        var questionResponses = questions.stream().map(question -> {
            var response = mapper.map(question, QuestionResponse.class);
            question.getAnswers().forEach(answer -> response.getAnswers().add(mapper.map(answer, AnswerResponse.class)));
            return response;
        }).collect(Collectors.toList());
        return TestDetailResponse.builder()
                .questionQuantity(request.getQuestionQuantity())
                .testDay(request.getTestDay())
                .subjectCode(subject.getCode())
                .subjectTitle(subject.getTitle())
                .questionResponses(questionResponses)
                .build();
    }

    @Override
    @Transactional
    public void createTestSecondStep(TestDetailResponse response) {
        var subject = subjectRepository.findByCode(response.getSubjectCode()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Code: %s not found !", response.getSubjectCode()), HttpStatus.NOT_FOUND)
        );
        var questions = response.getQuestionResponses().stream().map(
                questionResponse -> mapper.map(questionResponse, Question.class)
        ).collect(Collectors.toList());
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var test = Test.builder()
                .testDay(LocalDate.parse(response.getTestDay(), formatter))
                .build();
        test = testRepository.save(test);
        test.setQuestions(questions);
        test.setSubject(subject);
        testRepository.save(test);
    }

    @Override
    @Transactional
    public List<TestResponse> getAllTests() {
        var tests = testRepository.findByEnabledIsTrue();
        return tests.stream().map(
                test -> mapper.map(test, TestResponse.class)
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createTestSetFromTest(int testId, TestSetRequest request) {
        var test = testRepository.findById(testId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Test with id: %d not found !", testId), HttpStatus.NOT_FOUND));
        var questionNo = 0;
        for (var testNo = 1; testNo <= request.getTestSetQuantity(); ++testNo) {
            var testNoRootNumber = TEST_NO_ROOT_NUMBER;
            if (testSetRepository.existsByTestAndTestNo(test, testNo + testNoRootNumber)){
                testNoRootNumber = ((testNoRootNumber / 100) + 1) * testNoRootNumber;
            }
            var testSet = saveBlankTestSet(testNo + testNoRootNumber, test);
            Collections.shuffle(test.getQuestions());

            for (var question : test.getQuestions()) {
                var testSetQuestion = saveBlankTestSetQuestion(questionNo, testSet, question);
                saveAllTestSetQuestionAnswer(testSetQuestion, question);
                questionNo++;
            }
            questionNo = 0;
        }
    }

    private TestSet saveBlankTestSet(int testNo, Test test) {
        var testSet = TestSet.builder()
                .testNo(testNo)
                .test(test)
                .build();
        return testSetRepository.save(testSet);
    }

    private TestSetQuestion saveBlankTestSetQuestion(int questionNo, TestSet testSet, Question question) {
        var testSetQuestion = com.demo.app.model.TestSetQuestion.builder()
                .questionNo(questionNo)
                .question(question)
                .testSet(testSet)
                .build();
        return testSetQuestionRepository.save(testSetQuestion);
    }

    private void saveAllTestSetQuestionAnswer(TestSetQuestion testSetQuestion, Question question) {
        int answerNo = 0;
        var testSetQuestionAnswers = new ArrayList<TestSetQuestionAnswer>();
        Collections.shuffle(question.getAnswers());
        for (var answer : question.getAnswers()) {
            answerNo++;
            var testSetQuestionAnswer = TestSetQuestionAnswer.builder()
                    .answerNo(answerNo)
                    .testSetQuestion(testSetQuestion)
                    .answer(answer)
                    .build();
            testSetQuestionAnswers.add(testSetQuestionAnswer);
        }
        testSetQuestionAnswerRepository.saveAll(testSetQuestionAnswers);
    }
    @Override
    public void disableTest(int testId){
        var test = testRepository.findById(testId).orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with id %d", testId), HttpStatus.NOT_FOUND));
        test.setEnabled(false);
        testRepository.save(test);
    }
    @Override
    @Transactional
    public void updateTest(int testId, TestDetailRequest request){
        var test = testRepository.findById(testId).orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with id %d", testId), HttpStatus.NOT_FOUND));
        var questions = request.getQuestionResponses().stream().map(questionResponse -> mapper.map(questionResponse, Question.class) ).collect(Collectors.toList());
        test.setQuestions(questions);
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        test.setTestDay(LocalDate.parse(request.getTestDay(), formatter));
        testRepository.save(test);
    }

}
