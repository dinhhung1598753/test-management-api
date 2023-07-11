package com.demo.app.service.impl;

import com.demo.app.dto.testset.TestSetDetailResponse;
import com.demo.app.dto.testset.TestSetResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.*;
import com.demo.app.repository.TestRepository;
import com.demo.app.repository.TestSetQuestionRepository;
import com.demo.app.repository.TestSetRepository;
import com.demo.app.service.TestSetService;
import com.demo.app.util.word.WordUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestSetServiceImpl implements TestSetService {

    private final ModelMapper mapper;

    private final TestRepository testRepository;

    private final TestSetRepository testSetRepository;

    private final TestSetQuestionRepository testSetQuestionRepository;

    private static final Map<Integer, String> answerNoText = Map.of(
            1, "A",
            2, "B",
            3, "C",
            4, "D");

    @Override
    @Transactional
    public void createTestSetFromTest(int testId, Integer testSetQuantity) {
        @SuppressWarnings("DefaultLocale") var test = testRepository.findByIdAndEnabledIsTrue(testId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test with id: %d not found !", testId),
                        HttpStatus.NOT_FOUND));
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (var i = 1; i <= testSetQuantity; ++i) {
            var digit = i;
            executor.execute(() -> {
                int root = 100, testNo = digit + root;
                while (testSetRepository.existsByTestAndTestNoAndEnabledTrue(test, testNo)) {
                    root = (root / 100 + 1) * 100;
                    testNo = root + digit;
                }
                var testSet = TestSet.builder()
                        .testNo(testNo)
                        .test(test)
                        .build();
                var testSetQuestions = assignQuestionsNumber(testSet, test.getQuestions());
                testSet.setTestSetQuestions(testSetQuestions);
                testSetRepository.save(testSet);
            });
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
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
    public TestSetDetailResponse getTestSetDetail(Integer testId,Integer testNo) {
        var testSet = testSetRepository.findByTestIdAndTestNo(testId, testNo)
                .orElseThrow(() -> new EntityNotFoundException("TestSet not found !", HttpStatus.NOT_FOUND));
        return mapTestSetToDetailResponse(testSet);
    }

    @Override
    public ByteArrayInputStream exportTestSetToWord(int testSetId) throws IOException {
        @SuppressWarnings("DefaultLocale") var testSet = testSetRepository.findById(testSetId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test set with id %d not found !", testSetId),
                        HttpStatus.NOT_FOUND));
        var response = mapTestSetToDetailResponse(testSet);
        return WordUtils.convertTestToWord(response);
    }

    private TestSetDetailResponse mapTestSetToDetailResponse(TestSet testSet) {
        var testSetQuestions = testSetQuestionRepository.findByTestSetAndEnabledIsTrue(testSet);
        var questionResponses = testSetQuestions.parallelStream()
                .map(testSetQuestion -> {
                    var questionResponse = mapper.map(
                            testSetQuestion.getQuestion(),
                            TestSetDetailResponse.TestSetQuestionResponse.class);
                    questionResponse.setQuestionNo(testSetQuestion.getQuestionNo());
                    var answers = testSetQuestion.getTestSetQuestionAnswers().iterator();
                    questionResponse.getAnswers()
                            .forEach(responseAnswer -> {
                                var answer = answers.next();
                                responseAnswer.setContent(answer.getAnswer().getContent());
                                responseAnswer.setAnswerNo(answerNoText.get(answer.getAnswerNo()));
                            });
                    return questionResponse;
                })
                .collect(Collectors.toList());
        return TestSetDetailResponse.builder()
                .questions(questionResponses)
                .testSet(mapTestSetToResponse(testSet))
                .build();
    }

    private TestSetResponse mapTestSetToResponse(TestSet testSet) {
        var testSetResponse = mapper.map(testSet, TestSetResponse.class);
        var test = testSet.getTest();
        var subject = test.getSubject();

        testSetResponse.setTestDay(test.getTestDay().toString());
        testSetResponse.setDuration(test.getDuration());
        testSetResponse.setQuestionQuantity(test.getQuestionQuantity());
        testSetResponse.setSubjectTitle(subject.getTitle());
        testSetResponse.setSubjectCode(subject.getCode());
        return testSetResponse;
    }

}
