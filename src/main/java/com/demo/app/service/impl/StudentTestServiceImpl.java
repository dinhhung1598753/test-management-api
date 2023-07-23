package com.demo.app.service.impl;

import com.demo.app.dto.offline.OfflineExam;
import com.demo.app.dto.studentTest.QuestionSelectedAnswer;
import com.demo.app.dto.studentTest.StudentTestDetailResponse;
import com.demo.app.dto.studentTest.StudentTestFinishRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.InvalidRoleException;
import com.demo.app.model.*;
import com.demo.app.repository.*;
import com.demo.app.service.StudentTestService;
import com.demo.app.util.constant.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentTestServiceImpl implements StudentTestService {

    private static final String PYTHON_JSON_RESPONSE_FILE = "data.json";

    private final ObjectMapper objectMapper;

    private final ModelMapper modelMapper;

    private final ExamClassRepository examClassRepository;

    private final StudentRepository studentRepository;

    private final StudentTestRepository studentTestRepository;

    private final TestSetRepository testSetRepository;

    private final StudentTestDetailRepository studentTestDetailRepository;

    private final TestSetQuestionRepository testSetQuestionRepository;

    @Override
    @Transactional
    public StudentTestDetailResponse attemptTest(String classCode, Principal principal) {
        var examClass = examClassRepository.findByCodeAndEnabledIsTrue(classCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class with code %s not found !", classCode),
                        HttpStatus.NOT_FOUND));
        var student = studentRepository.findByUsernameAndEnabledIsTrue(principal.getName())
                .orElseThrow(() -> new InvalidRoleException(
                        "You don't have role to do this action!",
                        HttpStatus.FORBIDDEN));
        var studentTest = studentTestRepository.findStudentTestsByStudentAndStateAndExamClassIdAndEnabledIsTrue(
                student,
                State.IN_PROGRESS,
                examClass.getId()
        );
        if (studentTest != null) {
            return mapTestSetToResponse(studentTest.getTestSet());
        }
        return attemptNewTest(examClass, student);
    }

    private StudentTestDetailResponse attemptNewTest(ExamClass examClass, Student student) {
        var test = examClass.getTest();
        var pageable = PageRequest.of(0, 1);
        var testSet = testSetRepository.findRandomTestSetByTest(test.getId(), pageable)
                .stream().toList().get(0);
        var studentTest = StudentTest.builder()
                .student(student)
                .testSet(testSet)
                .testDate(LocalDate.now())
                .state(State.IN_PROGRESS)
                .examClassId(examClass.getId())
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
                            .forEach(questionAnswer -> {
                                var answerNo = Constant.ANSWER_TEXTS.get(questionAnswer.getAnswerNo());
                                answers.next().setAnswerNo(answerNo);
                            });
                    return question;
                })
                .toList();
        return StudentTestDetailResponse.builder()
                .testNo(testSet.getTestNo())
                .questions(questions)
                .build();
    }

    @Override
    public void finishStudentTest(StudentTestFinishRequest request, Principal principal) throws InterruptedException {
        var student = studentRepository.findByUsernameAndEnabledIsTrue(principal.getName())
                .orElseThrow(() -> new InvalidRoleException(
                        "You don't have role to do this action!",
                        HttpStatus.FORBIDDEN));
        var studentTest = studentTestRepository.findStudentTestsByStudentAndStateAndExamClassIdAndEnabledIsTrue(
                student,
                State.IN_PROGRESS,
                request.getExamClassId()
        );
        var binarySelectedAnswers = request.getQuestions()
                .parallelStream()
                .map(question -> QuestionSelectedAnswer.builder()
                        .selectedAnswer(convertSelectedTextToBinary(question.getSelectedAnswerNo()))
                        .questionNo(question.getQuestionNo())
                        .build())
                .collect(Collectors.toList());
        var markingThread = new Thread(() -> saveStudentTest(binarySelectedAnswers, studentTest));
        var saveStudentTestThread = new Thread(() -> saveStudentTestDetail(binarySelectedAnswers, studentTest));
        markingThread.start();
        saveStudentTestThread.start();

        markingThread.join();
        saveStudentTestThread.join();

    }

    private void saveStudentTest(List<QuestionSelectedAnswer> questionSelectedAnswers,
                                 StudentTest studentTest) {
        var testSet = studentTest.getTestSet();
        var test = testSet.getTest();
        var correctedAnswers = testSetQuestionRepository
                .findByTestSetAndEnabledIsTrue(testSet)
                .parallelStream()
                .collect(Collectors.toMap(
                        TestSetQuestion::getQuestionNo,
                        TestSetQuestion::getBinaryAnswer
                ));
        var mark = markStudentTestOnline(questionSelectedAnswers, correctedAnswers);
        var grade = new DecimalFormat("#.0")
                .format((double) mark / testSet.getTest().getQuestionQuantity() * test.getTotalPoint());
        studentTest.setMark(mark);
        studentTest.setGrade(Double.parseDouble(grade));
        studentTest.setState(State.FINISHED);
        studentTestRepository.save(studentTest);
    }

    private int markStudentTestOnline(List<QuestionSelectedAnswer> selectedAnswers, Map<Integer, String> correctedAnswers) {
        return (int) selectedAnswers.parallelStream()
                .filter(selectedAnswer -> {
                    String corrected = correctedAnswers.get(selectedAnswer.getQuestionNo());
                    return corrected.equals(selectedAnswer.getSelectedAnswer());
                }).count();
    }

    private void saveStudentTestDetail(List<QuestionSelectedAnswer> questionSelectedAnswers,
                                       StudentTest studentTest) {
        var testSet = studentTest.getTestSet();
        var studentTestDetails = questionSelectedAnswers
                .parallelStream()
                .map(selectedAnswer -> {
                    var testSetQuestion = testSetQuestionRepository.findByTestSetAndQuestionNo(
                            testSet,
                            selectedAnswer.getQuestionNo());
                    return StudentTestDetail.builder()
                            .studentTest(studentTest)
                            .selectedAnswer(selectedAnswer.getSelectedAnswer())
                            .testSetQuestion(testSetQuestion)
                            .build();
                }).collect(Collectors.toList());
        studentTestDetailRepository.saveAll(studentTestDetails);
    }

    @Override
    public List<OfflineExam> autoReadStudentOfflineExam(String classCode)
            throws IOException, InterruptedException {
        runModelPython(classCode);
        return getHandleResults(classCode);
    }

    private void runModelPython(String classCode) throws IOException, InterruptedException {
        var processBuilder = new ProcessBuilder("python", "main2.py", classCode);
        processBuilder.redirectErrorStream(true);
        var process = processBuilder.start();
        var results = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(results);
        process.waitFor();
    }

    private List<OfflineExam> getHandleResults(String classCode) throws IOException {
        try (var paths = Files.list(Paths.get("images/answer_sheets/" + classCode))) {
            var fileNames = paths.parallel()
                    .filter(path -> !Files.isDirectory(path))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toSet());
            return fileNames.parallelStream()
                    .map(fileName -> {
                        var responseFilePath = "json/" + fileName + "/" + PYTHON_JSON_RESPONSE_FILE;
                        var fileDataJson = new File(responseFilePath);
                        try {
                            var offlineExam = objectMapper.readValue(fileDataJson, OfflineExam.class);
                            FileUtils.deleteDirectory(fileDataJson.getParentFile());
                            return offlineExam;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void markStudentOfflineTest(OfflineExam offlineExam) {
        var examClass = examClassRepository.findByCodeAndEnabledIsTrue(offlineExam.getClassCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class %s not found !", offlineExam.getClassCode()),
                        HttpStatus.NOT_FOUND)
                );
        var testSet = testSetRepository.findByTestAndTestNoAndEnabledTrue(
                        examClass.getTest(),
                        offlineExam.getTestNo()
                ).orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test %s not found !", offlineExam.getTestNo()),
                        HttpStatus.NOT_FOUND)
        );
        var student = studentRepository.findByCodeAndEnabledIsTrue(offlineExam.getStudentCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Student %s not found !", offlineExam.getStudentCode()),
                        HttpStatus.NOT_FOUND)
                );
        var test = testSet.getTest();
        var questionAnswers = testSetQuestionRepository
                .findByTestSetAndEnabledIsTrue(testSet).stream()
                .collect(Collectors.toMap(
                        TestSetQuestion::getQuestionNo,
                        TestSetQuestion::getBinaryAnswer
                ));
        var mark = markStudentTestOffline(offlineExam.getAnswers(), questionAnswers);
        var grade = (double) mark / test.getQuestionQuantity() * test.getTotalPoint();
        var roundedGrade = new DecimalFormat("#.0").format(grade);
        var studentTest = StudentTest.builder()
                .student(student)
                .testSet(testSet)
                .mark(mark)
                .grade(Double.parseDouble(roundedGrade))
                .testDate(test.getTestDay())
                .examClassId(examClass.getId())
                .state(State.FINISHED)
                .build();
        var studentTestDetails = offlineExam.getAnswers()
                .parallelStream()
                .map(offlineAnswer -> {
                    var testSetQuestion = testSetQuestionRepository
                            .findByTestSetAndQuestionNo(testSet, offlineAnswer.getQuestionNo());
                    return StudentTestDetail.builder()
                            .studentTest(studentTest)
                            .selectedAnswer(offlineAnswer.getIsSelected())
                            .testSetQuestion(testSetQuestion)
                            .build();
                }).collect(Collectors.toList());

        studentTest.setStudentTestDetails(studentTestDetails);
        studentTestRepository.save(studentTest);
    }

    private int markStudentTestOffline(List<OfflineExam.OfflineAnswer> offlineAnswers,
                                       Map<Integer, String> correctedAnswers) {
        return (int) offlineAnswers.parallelStream()
                .peek(offlineAnswer -> {
                    var selectedText = offlineAnswer.getIsSelected();
                    offlineAnswer.setIsSelected(convertSelectedTextToBinary(selectedText));
                })
                .filter(offlineAnswer -> {
                    String corrected = correctedAnswers.get(offlineAnswer.getQuestionNo());
                    return corrected.equals(offlineAnswer.getIsSelected());
                })
                .count();
    }

    private String convertSelectedTextToBinary(String selectedAnswerNo) {
        var stringBuilder = new StringBuilder();
        var sortedAnswerNoText = Constant.ANSWER_TEXTS
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, HashMap::new));
        sortedAnswerNoText.forEach((no, text) ->
                stringBuilder.append(selectedAnswerNo.contains(text) ? "1" : "0"));
        return stringBuilder.toString();
    }

}
