package com.demo.app.service.impl;

import com.demo.app.dto.testset.TestSetDetailResponse;
import com.demo.app.dto.testset.TestSetQuestionResponse;
import com.demo.app.dto.testset.TestSetRequest;
import com.demo.app.dto.testset.TestSetResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.*;
import com.demo.app.repository.TestRepository;
import com.demo.app.repository.TestSetQuestionAnswerRepository;
import com.demo.app.repository.TestSetQuestionRepository;
import com.demo.app.repository.TestSetRepository;
import com.demo.app.service.TestSetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestSetServiceImpl implements TestSetService {

    private static final int TEST_NO_ROOT_NUMBER = 100;

    private final ModelMapper mapper;

    private final TestRepository testRepository;

    private final TestSetRepository testSetRepository;

    private final TestSetQuestionRepository testSetQuestionRepository;

    private final TestSetQuestionAnswerRepository testSetQuestionAnswerRepository;

    @Override
    @Transactional
    public void createTestSetFromTest(int testId, TestSetRequest request) {
        var test = testRepository.findById(testId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Test with id: %d not found !", testId), HttpStatus.NOT_FOUND));
        var questionNo = 1;
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
            questionNo = 1;
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
        var testSetQuestion = TestSetQuestion.builder()
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
    public List<TestSetResponse> getAllTestSet(){
        var testsets = testSetRepository.findByEnabledIsTrue();
        return testsets.stream().map(testSet -> {
            var test = testSet.getTest();
            var subject = test.getSubject();
            var testSetResponse = mapper.map(testSet, TestSetResponse.class);

            testSetResponse.setTestDay(test.getTestDay().toString());
            testSetResponse.setQuestionQuantity(test.getQuestionQuantity());
            testSetResponse.setSubjectTitle(subject.getTitle());
            testSetResponse.setSubjectCode(subject.getCode());
            return testSetResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public TestSetDetailResponse getTestSetDetailResponse(int testSetId){
        var testSet = testSetRepository.findById(testSetId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Test set with id %d not found !", testSetId),HttpStatus.NOT_FOUND));
        var questionResponses = new ArrayList<TestSetQuestionResponse>();
        testSet.getTestSetQuestions().forEach(testSetQuestion -> {
            var questionResponse = mapper.map(testSetQuestion, TestSetQuestionResponse.class);
            var answers = testSetQuestion.getTestSetQuestionAnswers();

            for (var i = 0; i < answers.size(); i++){
                String content = answers.get(i).getAnswer().getContent();
                questionResponse.getAnswers().get(i).setContent(content);
            }
            questionResponses.add(questionResponse);
        });

        return TestSetDetailResponse.builder()
                .duration(testSet.getTest().getDuration())
                .questions(questionResponses)
                .build();
    }

}
