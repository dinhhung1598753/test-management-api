package com.demo.app.service.impl;

import com.demo.app.dto.offline.OfflineExam;
import com.demo.app.dto.offline.OfflineExamRequest;
import com.demo.app.dto.offline.OfflineExamResponse;
import com.demo.app.dto.studentTest.QuestionSelectedAnswer;
import com.demo.app.dto.studentTest.StudentTestAttemptResponse;
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

    private final TestSetQuestionRepository testSetQuestionRepository;

    private final StudentTestDetailRepository studentTestDetailRepository;

    @Override
    @Transactional
    public StudentTestAttemptResponse attemptTest(String classCode, Principal principal) {
        var examClass = examClassRepository.findByCodeAndEnabledIsTrue(classCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class with code %s not found !", classCode),
                        HttpStatus.NOT_FOUND));
        var student = studentRepository.findByUsernameAndEnabledIsTrue(principal.getName())
                .orElseThrow(() -> new InvalidRoleException(
                        "You don't have role to do this action!",
                        HttpStatus.FORBIDDEN));
        var studentTest = studentTestRepository.findStudentTestsByStudentAndStateAndExamClassId(
                student,
                State.IN_PROGRESS,
                examClass.getId()
        );
        if (studentTest != null) {
            return mapTestSetToAttemptResponse(studentTest.getTestSet());
        }
        return attemptNewTest(examClass, student);
    }

    private StudentTestAttemptResponse attemptNewTest(ExamClass examClass, Student student) {
        var test = examClass.getTest();
        var pageable = PageRequest.of(0, 1);
        var testSet = testSetRepository.findRandomTestSetByTest(test.getId(), pageable)
                .stream().toList().get(0);
        var studentTest = StudentTest.builder()
                .student(student).testSet(testSet)
                .testDate(LocalDate.now())
                .state(State.IN_PROGRESS)
                .examClassId(examClass.getId())
                .build();
        studentTestRepository.save(studentTest);
        return mapTestSetToAttemptResponse(testSet);
    }

    private StudentTestAttemptResponse mapTestSetToAttemptResponse(TestSet testSet) {
        var questions = testSet.getTestSetQuestions()
                .parallelStream().map(testSetQuestion -> {
                    var questionResponse = modelMapper.map(
                            testSetQuestion.getQuestion(),
                            StudentTestAttemptResponse.StudentTestQuestion.class);
                    questionResponse.setQuestionNo(testSetQuestion.getQuestionNo());
                    var answerResponses = questionResponse.getAnswers().iterator();
                    testSetQuestion.getTestSetQuestionAnswers()
                            .forEach(questionAnswer -> {
                                var answerResponse = answerResponses.next();
                                var answerNo = Constant.ANSWER_TEXTS.get(questionAnswer.getAnswerNo());
                                var content = questionAnswer.getAnswer().getContent();
                                answerResponse.setAnswerNo(answerNo);
                                answerResponse.setContent(content);
                            });
                    return questionResponse;
                }).toList();
        return StudentTestAttemptResponse.builder()
                .testNo(testSet.getTestNo())
                .questions(questions)
                .build();
    }

    @Override
    @Transactional
    public void finishStudentTest(StudentTestFinishRequest request,
                                  Principal principal) {
        var student = studentRepository.findByUsernameAndEnabledIsTrue(principal.getName())
                .orElseThrow(() -> new InvalidRoleException(
                        "You don't have role to do this action!",
                        HttpStatus.FORBIDDEN));
        var studentTest = studentTestRepository.findStudentTestsByStudentAndStateAndExamClassId(
                student,
                State.IN_PROGRESS,
                request.getExamClassId()
        );
        var testSet = studentTest.getTestSet();
        var test = testSet.getTest();
        var binarySelectedAnswers = request.getQuestions()
                .parallelStream().map(question -> {
                    var selectedAnswerNo = question.getSelectedAnswerNo();
                    var binaryAnswer = convertSelectedTextToBinary(selectedAnswerNo);
                    return QuestionSelectedAnswer.builder()
                            .selectedAnswer(binaryAnswer)
                            .questionNo(question.getQuestionNo())
                            .build();
                }).collect(Collectors.toList());
        var correctedAnswers = testSetQuestionRepository.findByTestSet(testSet)
                .parallelStream().collect(Collectors.toMap(
                        TestSetQuestion::getQuestionNo,
                        TestSetQuestion::getBinaryAnswer
                ));
        var mark = markStudentTestOnline(binarySelectedAnswers, correctedAnswers);
        var grade = ((double) mark / test.getQuestionQuantity()) * test.getTotalPoint();
        var roundedGrade = new DecimalFormat("#.0").format(grade);
        studentTest.setMark(mark);
        studentTest.setGrade(Double.parseDouble(roundedGrade));
        studentTest.setState(State.FINISHED);
        var studentTestDetails = binarySelectedAnswers.parallelStream()
                .map(selectedAnswer -> {
                    var testSetQuestion = testSetQuestionRepository.findByTestSetAndQuestionNo(
                            testSet,
                            selectedAnswer.getQuestionNo()
                    );
                    return StudentTestDetail.builder()
                            .selectedAnswer(selectedAnswer.getSelectedAnswer())
                            .isCorrected(selectedAnswer.getIsCorrected())
                            .testSetQuestion(testSetQuestion)
                            .studentTest(studentTest)
                            .build();
                }).collect(Collectors.toList());
        studentTest.setStudentTestDetails(studentTestDetails);
        studentTestRepository.save(studentTest);
    }

    private int markStudentTestOnline(List<QuestionSelectedAnswer> binarySelectedAnswers,
                                      Map<Integer, String> correctedAnswers) {
        return (int) binarySelectedAnswers.parallelStream()
                .filter(selectedAnswer -> {
                    var corrected = correctedAnswers.get(selectedAnswer.getQuestionNo());
                    var isCorrected = selectedAnswer.getSelectedAnswer().equals(corrected);
                    selectedAnswer.setIsCorrected(isCorrected);
                    return isCorrected;
                }).count();
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
                    .toList();
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
    public OfflineExamResponse markStudentOfflineTest(OfflineExamRequest request) {
        var examClass = examClassRepository.findByCodeAndEnabledIsTrue(request.getClassCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class %s not found !", request.getClassCode()),
                        HttpStatus.NOT_FOUND));
        var testSet = testSetRepository.findByTestAndTestNoAndEnabledTrue(examClass.getTest(), request.getTestNo())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test %s not found !", request.getTestNo()),
                        HttpStatus.NOT_FOUND));
        var student = studentRepository.findByCodeAndEnabledIsTrue(request.getStudentCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Student %s not found !", request.getStudentCode()),
                        HttpStatus.NOT_FOUND));
        var test = testSet.getTest();
        var questionAnswers = testSetQuestionRepository
                .findByTestSet(testSet).stream()
                .collect(Collectors.toMap(
                        TestSetQuestion::getQuestionNo,
                        TestSetQuestion::getBinaryAnswer));
        var mark = markStudentTestOffline(request.getAnswers(), questionAnswers);
        var grade = ((double) mark / test.getQuestionQuantity()) * test.getTotalPoint();
        var roundedGrade = new DecimalFormat("#.0").format(grade);
        grade = Double.parseDouble(roundedGrade);
        var studentTest = StudentTest.builder()
                .student(student).testSet(testSet)
                .mark(mark).grade(grade)
                .testDate(test.getTestDay())
                .examClassId(examClass.getId())
                .state(State.FINISHED)
                .build();
        var studentTestDetails = request.getAnswers()
                .parallelStream().map(offlineAnswer -> {
                    var testSetQuestion = testSetQuestionRepository
                            .findByTestSetAndQuestionNo(testSet, offlineAnswer.getQuestionNo());
                    return StudentTestDetail.builder()
                            .studentTest(studentTest)
                            .selectedAnswer(offlineAnswer.getIsSelected())
                            .isCorrected(offlineAnswer.getIsCorrected())
                            .testSetQuestion(testSetQuestion)
                            .build();
                }).collect(Collectors.toList());

        studentTest.setStudentTestDetails(studentTestDetails);
        studentTestRepository.save(studentTest);
        var response = modelMapper.map(request, OfflineExamResponse.class);
        response.setGrade(grade);
        response.setMark(mark);
        response.setTotalPoint(test.getTotalPoint());
        return response;
    }

    private int markStudentTestOffline(List<OfflineExamRequest.OfflineAnswer> offlineAnswers,
                                       Map<Integer, String> correctedAnswers) {
        return (int) offlineAnswers.parallelStream()
                .peek(offlineAnswer -> {
                    var selectedText = offlineAnswer.getIsSelected();
                    offlineAnswer.setIsSelected(convertSelectedTextToBinary(selectedText));
                }).filter(offlineAnswer -> {
                    String corrected = correctedAnswers.get(offlineAnswer.getQuestionNo());
                    var isCorrected = offlineAnswer.getIsSelected().equals(corrected);
                    offlineAnswer.setIsCorrected(isCorrected);
                    return isCorrected;
                }).count();
    }

    private String convertSelectedTextToBinary(String selectedAnswerNo) {
        var stringBuilder = new StringBuilder();
        var sortedAnswerNoText = Constant.ANSWER_TEXTS
                .entrySet().stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, HashMap::new));
        sortedAnswerNoText.forEach((no, text) -> stringBuilder.append(
                selectedAnswerNo.contains(text) ? "1" : "0"));
        return stringBuilder.toString();
    }

    @Override
    public StudentTestDetailResponse getStudentTestDetail(Integer studentTestId){
        var studentTest = studentTestRepository.findByIdAndEnabledIsTrue(studentTestId)
                .orElseThrow(() -> new EntityNotFoundException("Student Test not found !", HttpStatus.NOT_FOUND));
        var studentTestDetails = studentTestDetailRepository.findByStudentTest(studentTest).iterator();
        var testAttempted = mapTestSetToAttemptResponse(studentTest.getTestSet());
        var testDetail = modelMapper.map(testAttempted, StudentTestDetailResponse.class);
        testDetail.setGrade(studentTest.getGrade());
        testDetail.setMark(studentTest.getMark());
        testDetail.getQuestions().forEach(question -> {
            var studentTestDetail = studentTestDetails.next();
            question.setIsCorrected(studentTestDetail.getIsCorrected());
            var isSelects = convertBinaryToSelected(studentTestDetail.getSelectedAnswer()).iterator();
            question.getAnswers().forEach(answer -> answer.setIsSelected(isSelects.next()));
        });
        return testDetail;
    }

    private List<Boolean> convertBinaryToSelected(String selectedAnswer){
        return selectedAnswer.chars().mapToObj(e->(char)e)
                .toList().parallelStream()
                .map(binaryLetter -> binaryLetter.equals('1'))
                .collect(Collectors.toList());
    }

}