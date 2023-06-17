package com.demo.app.service.impl;

import com.demo.app.dto.testset.TestSetDetailResponse;
import com.demo.app.dto.testset.TestSetQuestionResponse;
import com.demo.app.dto.testset.TestSetRequest;
import com.demo.app.dto.testset.TestSetResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.*;
import com.demo.app.repository.TestRepository;
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

    private final ModelMapper mapper;

    private final TestRepository testRepository;

    private final TestSetRepository testSetRepository;

    @Override
    @Transactional
    public void createTestSetFromTest(int testId, TestSetRequest request) {
        var test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test with id: %d not found !", testId),
                        HttpStatus.NOT_FOUND));
        var testSetQuantity = request.getTestSetQuantity();
        var testSets = new ArrayList<TestSet>();
        for (var digit = 1; digit <= testSetQuantity; ++digit) {
            int root = 100, testNo = digit + root;
            if (testSetRepository.existsByTestAndTestNoAndEnabledTrue(test, testNo)) {
                root = (root / 100 + 1) * 100;
                testNo = root + digit;
            }
            var testSet = TestSet.builder()
                    .testNo(testNo)
                    .test(test)
                    .build();
            testSet.setTestSetQuestions(assignQuestionsNumber(testSet, test.getQuestions()));
            testSets.add(testSet);
        }
        testSetRepository.saveAll(testSets);
    }


    private List<TestSetQuestion> assignQuestionsNumber(TestSet testset, List<Question> questions) {
        var testSetQuestions = new ArrayList<TestSetQuestion>();
        var questionNo = 1;
        Collections.shuffle(questions);
        for (var question : questions) {
            var testSetQuestion = new TestSetQuestion();
            testSetQuestion.setTestSet(testset);
            testSetQuestion.setQuestion(question);
            testSetQuestion.setQuestionNo(questionNo++);
            testSetQuestion.setTestSetQuestionAnswers(assignAnswersNumber(testSetQuestion, question.getAnswers()));
            testSetQuestion.setBinaryAnswer(binaryAnswer(testSetQuestion.getTestSetQuestionAnswers()));
            testSetQuestions.add(testSetQuestion);
        }
        return testSetQuestions;
    }

    private List<TestSetQuestionAnswer> assignAnswersNumber(TestSetQuestion testSetQuestion, List<Answer> answers) {
        var testSetAnswers = new ArrayList<TestSetQuestionAnswer>();
        var answerNo = 1;
        Collections.shuffle(answers);
        for (var answer : answers) {
            var testSetAnswer = new TestSetQuestionAnswer();
            testSetAnswer.setAnswer(answer);
            testSetAnswer.setAnswerNo(answerNo++);
            testSetAnswer.setTestSetQuestion(testSetQuestion);
            testSetAnswers.add(testSetAnswer);
        }
        return testSetAnswers;
    }

    private String binaryAnswer(List<TestSetQuestionAnswer> testSetAnswers) {
        var stringBuilder = new StringBuilder();
        testSetAnswers.forEach(testSetAnswer -> {
            var isCorrected = testSetAnswer.getAnswer().getIsCorrected();
            stringBuilder.append(isCorrected ? "1" : "0");
        });
        return stringBuilder.toString();
    }


    @Override
    public List<TestSetResponse> getAllTestSet() {
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
    public TestSetDetailResponse getTestSetDetail(int testSetId) {
        var testSet = testSetRepository.findById(testSetId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test set with id %d not found !", testSetId),
                        HttpStatus.NOT_FOUND));
        var questionResponses = new ArrayList<TestSetQuestionResponse>();
        testSet.getTestSetQuestions()
                .forEach(testSetQuestion -> {
                    var questionResponse = mapper.map(testSetQuestion, TestSetQuestionResponse.class);
                    var question = testSetQuestion.getQuestion();
                    var answers = testSetQuestion.getTestSetQuestionAnswers();

                    questionResponse.setLevel(question.getLevel().toString());
                    questionResponse.setTopicText(question.getTopicText());
                    questionResponse.setTopicImage(question.getTopicImage());
                    for (var i = 0; i < answers.size(); i++) {
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
