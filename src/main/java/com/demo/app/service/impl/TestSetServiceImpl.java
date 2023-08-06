package com.demo.app.service.impl;

import com.demo.app.dto.testset.TestSetDetailResponse;
import com.demo.app.dto.testset.TestSetResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.*;
import com.demo.app.repository.*;
import com.demo.app.service.TestSetService;
import com.demo.app.util.constant.Constant;
import com.demo.app.util.word.WordUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestSetServiceImpl implements TestSetService {

    private final ModelMapper mapper;

    private final TestRepository testRepository;

    private final AnswerRepository answerRepository;

    private final TestSetRepository testSetRepository;

    private final TestSetQuestionRepository testSetQuestionRepository;

    @Override
    @Transactional
    public List<Integer> createTestSetFromTest(int testId, Integer testSetQuantity) {
        var test = testRepository.findByIdAndEnabledIsTrue(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found !", HttpStatus.NOT_FOUND));
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        var futures = new ArrayList<Future<Integer>>();
        for (var i = 1; i <= testSetQuantity; ++i) {
            var digit = i;
            var future = executor.submit(() -> {
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
                return testSetRepository.save(testSet).getTestNo();
            });
            futures.add(future);
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        return futures.parallelStream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (ExecutionException | InterruptedException ee) {
                        throw new RuntimeException();
                    }
                }).collect(Collectors.toList());
    }

    private List<TestSetQuestion> assignQuestionsNumber(TestSet testset,
                                                        List<Question> questions) {
        questions = shuffleQuestions(questions);
        var testSetQuestions = questions.parallelStream()
                .map(question -> {
                    var testSetQuestion = TestSetQuestion.builder()
                            .testSet(testset)
                            .question(question)
                            .build();
                    var answers = answerRepository.findByQuestionAndEnabledIsTrue(question);
                    var testSetQuestionAnswers = assignAnswersNumber(testSetQuestion, answers);
                    var binaryAnswer = binaryAnswer(testSetQuestionAnswers);
                    testSetQuestion.setTestSetQuestionAnswers(testSetQuestionAnswers);
                    testSetQuestion.setBinaryAnswer(binaryAnswer);
                    return testSetQuestion;
                })
                .collect(Collectors.toList());
        var questionNo = 1;
        for (var testSetQuestion : testSetQuestions)
            testSetQuestion.setQuestionNo(questionNo++);
        return testSetQuestions;
    }

    private List<Question> shuffleQuestions(List<Question> questions) {
        return questions.parallelStream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), ques -> {
                    Collections.shuffle(ques);
                    return ques;
                }));
    }

    private List<TestSetQuestionAnswer> assignAnswersNumber(TestSetQuestion testSetQuestion,
                                                            List<Answer> answers) {
        Collections.shuffle(answers);
        var testSetAnswers = answers.stream()
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
    public TestSetDetailResponse getTestSetDetail(Integer testId, Integer testNo) {
        var testSet = testSetRepository.findByTestIdAndTestNoAndEnabledIsTrue(testId, testNo)
                .orElseThrow(() -> new EntityNotFoundException("TestSet not found !", HttpStatus.NOT_FOUND));
        return mapTestSetToDetailResponse(testSet);
    }

    @Override
    public ByteArrayInputStream exportTestSetToWord(Integer testId, Integer testNo) throws IOException {
        @SuppressWarnings("DefaultLocale") var testSet = testSetRepository.findByTestIdAndTestNoAndEnabledIsTrue(testId, testNo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Test set not found !",
                        HttpStatus.NOT_FOUND));
        var response = mapTestSetToDetailResponse(testSet);
        return WordUtils.convertTestToWord(response);
    }

    private TestSetDetailResponse mapTestSetToDetailResponse(TestSet testSet) {
        var testSetQuestions = testSetQuestionRepository.findByTestSetOrderByQuestionNoAsc(testSet);
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
                                responseAnswer.setAnswerNo(Constant.ANSWER_TEXTS.get(answer.getAnswerNo()));
                            });
                    return questionResponse;
                }).collect(Collectors.toList());
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
