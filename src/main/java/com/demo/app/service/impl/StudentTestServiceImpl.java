package com.demo.app.service.impl;

import com.demo.app.dto.offline.OfflineAnswer;
import com.demo.app.dto.offline.OfflineExam;
import com.demo.app.dto.studentTest.StudentTestFinishRequest;
import com.demo.app.dto.studentTest.StudentTestDetailResponse;
import com.demo.app.dto.studentTest.QuestionSelectedAnswer;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.InvalidRoleException;
import com.demo.app.model.*;
import com.demo.app.repository.*;
import com.demo.app.service.StudentTestService;
import com.demo.app.util.constant.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentTestServiceImpl implements StudentTestService {

    private static final String PYTHON_JSON_RESPONSE_FILE = "data.json";

    private static final String PYTHON_RESULT_FILE = "result.txt";

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
        var examClass = examClassRepository.findByCode(classCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class with code %s not found !", classCode),
                        HttpStatus.NOT_FOUND));
        var student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new InvalidRoleException(
                        "You don't have role to do this action!",
                        HttpStatus.FORBIDDEN));
        var studentTest = studentTestRepository.findStudentTestsByStudentAndStateAndExamClassId(
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
                            .forEach(questionAnswer ->
                                    answers.next().setAnswerNo(
                                            Constant.answerNoText
                                                    .get(questionAnswer.getAnswerNo()))
                            );
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
        var student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new InvalidRoleException(
                        "You don't have role to do this action!",
                        HttpStatus.FORBIDDEN));
        var studentTest = studentTestRepository.findStudentTestsByStudentAndStateAndExamClassId(
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
        var correctedAnswers = testSetQuestionRepository
                .findByTestSetAndEnabledIsTrue(testSet)
                .parallelStream()
                .collect(Collectors.toMap(
                        TestSetQuestion::getQuestionNo,
                        TestSetQuestion::getBinaryAnswer
                ));
        var mark = markStudentTestOnline(questionSelectedAnswers, correctedAnswers);
        var grade = Math.round((double) mark / testSet.getTest().getQuestionQuantity() * 10) / 10;
        studentTest.setMark(mark);
        studentTest.setGrade(grade);
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
    public void autoMarkingStudentTest(String classCode) throws IOException {
        try (var paths = Files.list(Paths.get("images/answer_sheets/" + classCode))) {
            var fileNames = paths.parallel().filter(path -> !Files.isDirectory(path))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
            fileNames.forEach(fileName -> {
                System.out.println(fileName);
                try {
                    var response = runModelPython(fileName);
                    Thread.sleep(500);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private OfflineExam runModelPython(String imageName) throws IOException, InterruptedException {
        Files.deleteIfExists(Paths.get(PYTHON_JSON_RESPONSE_FILE));
        Files.deleteIfExists(Paths.get(PYTHON_RESULT_FILE));
        String commandLine = String.format("cmd /c python main.py %s", imageName);
        var process = Runtime.getRuntime().exec(commandLine);
        process.waitFor();
        return objectMapper.readValue(new File(PYTHON_JSON_RESPONSE_FILE), OfflineExam.class);
    }

    private void markingOfflineAnswer(OfflineExam offlineExam) {
        var examClass = examClassRepository.findByCode(offlineExam.getClassCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class %s not found !", offlineExam.getClassCode()),
                        HttpStatus.NOT_FOUND));
        var testset = testSetRepository.findByTestAndTestNoAndEnabledTrue(examClass.getTest(), offlineExam.getTestNo())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Test %s not found !", offlineExam.getTestNo()),
                        HttpStatus.NOT_FOUND));
        var student = studentRepository.findByCode(offlineExam.getStudentCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Student %s not found", offlineExam.getStudentCode()),
                        HttpStatus.NOT_FOUND));
        var questionAnswers = testSetQuestionRepository.findByTestSetAndEnabledIsTrue(testset)
                .stream()
                .collect(Collectors.toMap(
                        TestSetQuestion::getQuestionNo,
                        TestSetQuestion::getBinaryAnswer
                ));
        var mark = markStudentTestOffline(offlineExam.getAnswers(), questionAnswers);
        var studentTest = StudentTest.builder()
                .student(student)
                .testSet(testset)
                .mark(mark)
                .grade((double) mark / questionAnswers.size())
                .testDate(LocalDate.now())
                .examClassId(examClass.getId())
                .state(State.FINISHED)
                .build();
        var studentTestDetails = offlineExam.getAnswers()
                .parallelStream()
                .map(offlineAnswer -> {
                    var testSetQuestion = testSetQuestionRepository.findByTestSetAndQuestionNo(
                            testset,
                            offlineAnswer.getQuestionNo());
                    return StudentTestDetail.builder()
                            .studentTest(studentTest)
                            .selectedAnswer(offlineAnswer.getIsSelected())
                            .testSetQuestion(testSetQuestion)
                            .build();
                }).collect(Collectors.toList());

        studentTest.setStudentTestDetails(studentTestDetails);
        studentTestRepository.save(studentTest);
    }

    private int markStudentTestOffline(List<OfflineAnswer> offlineAnswers, Map<Integer, String> correctedAnswers) {
        return (int) offlineAnswers.parallelStream()
                .filter(offlineAnswer -> {
                    String corrected = correctedAnswers.get(offlineAnswer.getQuestionNo());
                    return corrected.equals(offlineAnswer.getIsSelected());
                })
                .count();
    }

    private String convertSelectedTextToBinary(String selectedAnswerNo) {
        var stringBuilder = new StringBuilder();
        var sortedAnswerNoText = Constant.answerNoText
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, HashMap::new));
        sortedAnswerNoText.forEach(
                (no, text) -> stringBuilder.append(selectedAnswerNo.contains(text) ? "1" : "0"));
        return stringBuilder.toString();
    }

}
