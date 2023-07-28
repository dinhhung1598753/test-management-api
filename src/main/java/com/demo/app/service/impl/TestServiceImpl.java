package com.demo.app.service.impl;

import com.demo.app.dto.test.*;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.Question;
import com.demo.app.model.Test;
import com.demo.app.model.TestSet;
import com.demo.app.repository.QuestionRepository;
import com.demo.app.repository.SubjectRepository;
import com.demo.app.repository.TestRepository;
import com.demo.app.repository.TestSetRepository;
import com.demo.app.service.TestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final QuestionRepository questionRepository;

    private final TestSetRepository testSetRepository;

    private final SubjectRepository subjectRepository;

    private final TestRepository testRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public Integer createTestRandomQuestion(TestRequest request) throws EntityNotFoundException {
        var subject = subjectRepository.findByCodeAndEnabledIsTrue(request.getSubjectCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Code: %s not found !", request.getSubjectCode()),
                        HttpStatus.NOT_FOUND));
        final int FIRST_RESULTS = 0;
        var pageable = PageRequest.of(FIRST_RESULTS, request.getQuestionQuantity());
        var questions = questionRepository.findQuestionBySubjectChapterOrder(
                request.getSubjectCode(),
                request.getChapterIds(),
                pageable
        );
        var test = Test.builder()
                .testDay(LocalDate.parse(request.getTestDay(), DATE_FORMATTER))
                .testTime(LocalTime.parse(request.getTestTime(), TIME_FORMATTER))
                .questions(questions.stream().parallel().toList())
                .questionQuantity(request.getQuestionQuantity())
                .totalPoint(request.getTotalPoint())
                .duration(request.getDuration())
                .subject(subject)
                .build();
        return testRepository.save(test).getId();
    }

    @Override
    @Transactional
    public Integer createTestByChooseQuestions(TestQuestionRequest request) {
        var questions = questionRepository.findAllById(request.getQuestionIds());
        if (questions.isEmpty()) {
            throw new EntityNotFoundException("Not found any question to add to test !", HttpStatus.NOT_FOUND);
        }
        var subject = questions.get(0).getChapter().getSubject();
        var test = Test.builder()
                .testDay(LocalDate.parse(request.getTestDay(), DATE_FORMATTER))
                .testTime(LocalTime.parse(request.getTestTime(), TIME_FORMATTER))
                .questionQuantity(questions.size())
                .duration(request.getDuration())
                .totalPoint(request.getTotalPoint())
                .build();
        test.setQuestions(questions);
        test.setSubject(subject);
        return testRepository.save(test).getId();
    }

    @Override
    @Transactional
    public List<TestResponse> getAllTests() {
        var tests = testRepository.findByEnabledIsTrue();
        return tests.parallelStream()
                .map(test -> {
                    var testResponse = mapper.map(test, TestResponse.class);
                    var subject = test.getSubject();
                    var testSets = testSetRepository.findByEnabledIsTrueAndTest(test);
                    testResponse.setSubjectCode(subject.getCode());
                    testResponse.setSubjectTitle(subject.getTitle());
                    testResponse.setTestSetNos(
                            testSets.parallelStream()
                                    .map(TestSet::getTestNo)
                                    .collect(Collectors.toList())
                    );
                    return testResponse;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateTest(int testId, TestDetailRequest request) {
        var test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found !", HttpStatus.NOT_FOUND));
        var questions = request.getQuestionResponses()
                .stream()
                .map(questionResponse -> mapper.map(questionResponse, Question.class))
                .collect(Collectors.toList());
        test.setQuestions(questions);
        test.setTestDay(LocalDate.parse(request.getTestDay(), DATE_FORMATTER));
        test.setDuration(request.getDuration());
        testRepository.save(test);
    }

    @Override
    public void disableTest(int testId) {
        var test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found !", HttpStatus.NOT_FOUND));
        test.setEnabled(false);
        test.setQuestions(null);
        var testSets = testSetRepository.findByEnabledIsTrueAndTest(test);
        testSets.forEach(testSet -> testSet.setEnabled(false));
        testRepository.save(test);
        testSetRepository.saveAll(testSets);
    }

}
