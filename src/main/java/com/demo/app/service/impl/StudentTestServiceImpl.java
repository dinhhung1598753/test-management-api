package com.demo.app.service.impl;

import com.demo.app.dto.offline.OfflineAnswer;
import com.demo.app.dto.offline.OfflineExam;
import com.demo.app.dto.student_test.StudentTestDetailResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.InvalidRoleException;
import com.demo.app.exception.UserNotEnrolledException;
import com.demo.app.model.*;
import com.demo.app.repository.*;
import com.demo.app.service.StudentTestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentTestServiceImpl implements StudentTestService {

    private static final String OFFLINE_EXAM_JSON_PATH = "data.json";

    private static final String STUDENT_CODE_HEADER = "20";

    private final ObjectMapper objectMapper;

    private final ModelMapper modelMapper;

    private final ExamClassRepository examClassRepository;

    private final StudentRepository studentRepository;

    private final StudentTestRepository studentTestRepository;

    private final TestSetRepository testSetRepository;

    private final TestSetQuestionRepository testSetQuestionRepository;

    @Override
    public StudentTestDetailResponse attemptTest(String classCode, Principal principal) {
        var student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new InvalidRoleException(
                        "You don't have role to do this action!",
                        HttpStatus.FORBIDDEN));
        var examClass = examClassRepository.findByCode(classCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class with code %s not found !", classCode),
                        HttpStatus.NOT_FOUND));
        if (!examClass.getStudents().contains(student)) {
            throw new UserNotEnrolledException(
                    String.format("You are not in class %s", examClass.getCode()),
                    HttpStatus.FORBIDDEN);
        }

        var testSets = examClass.getTest().getTestSets();
        var testSet = getRandomTestSet(testSets);
        var studentTest = StudentTest.builder()
                .student(student)
                .testSet(testSet)
                .state(State.IN_PROGRESS)
                .build();
        studentTestRepository.save(studentTest);
        return mapTestSetToResponse(testSet);
    }

    private StudentTestDetailResponse mapTestSetToResponse(TestSet testSet) {
        var questions = testSet.getTestSetQuestions()
                .parallelStream()
                .map(testSetQuestion -> {
                    var question = modelMapper.map(
                            testSetQuestion.getQuestion(),
                            StudentTestDetailResponse.StudentTestQuestion.class);
                    question.setQuestionNo(testSetQuestion.getQuestionNo());
                    var answers = question.getAnswers().iterator();
                    testSetQuestion.getTestSetQuestionAnswers()
                            .forEach(questionAnswer ->
                                    answers.next().setAnswerNo(questionAnswer.getAnswerNo()));
                    return question;
                })
                .toList();
        return StudentTestDetailResponse.builder()
                .testNo(testSet.getTestNo())
                .questions(questions)
                .build();
    }

    private TestSet getRandomTestSet(List<TestSet> testSets) {
        var random = new Random();
        var size = testSets.size();
        return testSets.get(random.nextInt(size));
    }

    @Override
    @Transactional
    public void markingOfflineAnswer() throws IOException {
        var offlineExam = objectMapper.readValue(
                new File(OFFLINE_EXAM_JSON_PATH),
                OfflineExam.class);
        var examClass = examClassRepository.findByCode(offlineExam.getClassCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class %s not found !", offlineExam.getClassCode()),
                        HttpStatus.NOT_FOUND));
        var testset = testSetRepository.findByTestAndTestNoAndEnabledTrue(examClass.getTest(), offlineExam.getTestNo())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test %s not found !", offlineExam.getTestNo()),
                        HttpStatus.NOT_FOUND));
        String studentCode = STUDENT_CODE_HEADER + offlineExam.getStudentCode();
        var student = studentRepository.findByCode(studentCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Student %s not found", offlineExam.getStudentCode()),
                        HttpStatus.NOT_FOUND));
        var questionAnswers = testset.getTestSetQuestions()
                .stream()
                .collect(Collectors.toMap(
                        TestSetQuestion::getQuestionNo,
                        TestSetQuestion::getBinaryAnswer
                ));
        var mark = markStudentTest(offlineExam.getAnswers(), questionAnswers);
        var studentTest = StudentTest.builder()
                .student(student)
                .testSet(testset)
                .mark(mark)
                .grade((double) mark / questionAnswers.size())
                .build();
        var studentTestDetails = offlineExam.getAnswers()
                .parallelStream()
                .map(offlineAnswer -> {
                    var testSetQuestion = testSetQuestionRepository.findByTestSetAndQuestionNo(
                            testset,
                            offlineAnswer.getQuestionNo());
                    return StudentTestDetail.builder()
                            .studentTest(studentTest)
                            .selectedAnswer(offlineAnswer.getSelected())
                            .testSetQuestion(testSetQuestion)
                            .isCorrected(offlineAnswer.isCorrected())
                            .build();
                }).collect(Collectors.toList());

        studentTest.setStudentTestDetails(studentTestDetails);
        studentTestRepository.save(studentTest);
    }

    private int markStudentTest(List<OfflineAnswer> offlineAnswers, Map<Integer, String> correctedAnswers) {
        return (int) offlineAnswers.parallelStream()
                .peek(offlineAnswer -> {
                    String corrected = correctedAnswers.get(offlineAnswer.getQuestionNo());
                    offlineAnswer.setCorrected(corrected.equals(offlineAnswer.getSelected()));
                })
                .filter(offlineAnswer -> {
                    String corrected = correctedAnswers.get(offlineAnswer.getQuestionNo());
                    return corrected.equals(offlineAnswer.getSelected());
                })
                .count();
    }

}
