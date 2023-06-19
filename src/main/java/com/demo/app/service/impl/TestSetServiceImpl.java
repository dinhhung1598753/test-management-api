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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestSetServiceImpl implements TestSetService {

    private final ModelMapper mapper;

    private final TestRepository testRepository;

    private final TestSetRepository testSetRepository;

    @Override
    @Transactional
    public void createTestSetFromTest(int testId, TestSetRequest request) throws InterruptedException {
        var test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test with id: %d not found !", testId),
                        HttpStatus.NOT_FOUND));
        var testSetQuantity = request.getTestSetQuantity();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (var digit = 1; digit <= testSetQuantity; ++digit) {
            int finalDigit = digit;
            executor.execute(() -> {
                int root = 100, testNo = finalDigit + root;
                while (testSetRepository.existsByTestAndTestNoAndEnabledTrue(test, testNo)) {
                    root = (root / 100 + 1) * 100;
                    testNo = root + finalDigit;
                }
                var testSet = TestSet.builder().testNo(testNo).test(test)
                        .build();
                var testSetQuestions = assignQuestionsNumber(testSet, test.getQuestions());
                testSet.setTestSetQuestions(testSetQuestions);
                testSetRepository.save(testSet);

            });
        }

        Thread.sleep(5000);
    }


    private List<TestSetQuestion> assignQuestionsNumber(TestSet testset, List<Question> questions) {
        Collections.shuffle(questions);
        var testSetQuestions = questions.stream()
                .map(question -> {
                    var testSetQuestion = TestSetQuestion.builder()
                            .testSet(testset)
                            .question(question)
                            .build();
                    testSetQuestion.setTestSetQuestionAnswers(assignAnswersNumber(testSetQuestion, question.getAnswers()));
                    testSetQuestion.setBinaryAnswer(binaryAnswer(testSetQuestion.getTestSetQuestionAnswers()));
                    return testSetQuestion;
                })
                .collect(Collectors.toList());
        var questionNo = 1;
        for (var testSetQuestion : testSetQuestions)
            testSetQuestion.setQuestionNo(questionNo++);
        return testSetQuestions;
    }

    private List<TestSetQuestionAnswer> assignAnswersNumber(TestSetQuestion testSetQuestion, List<Answer> answers) {
        Collections.shuffle(answers);
        var testSetAnswers = answers.parallelStream()
                .map(answer -> TestSetQuestionAnswer.builder()
                        .answer(answer)
                        .testSetQuestion(testSetQuestion)
                        .build())
                .collect(Collectors.toList());
        var answerNo = 1;
        for (var testSetAnswer : testSetAnswers)
            testSetAnswer.setAnswerNo(answerNo++);
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
        return testsets.parallelStream().map(testSet -> {
            var testSetResponse = mapper.map(testSet, TestSetResponse.class);
            var test = testSet.getTest();
            var subject = test.getSubject();

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
