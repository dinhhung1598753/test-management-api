package com.demo.app.service.impl;

import com.demo.app.dto.offline.OfflineAnswer;
import com.demo.app.dto.offline.OfflineExam;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.InvalidRoleException;
import com.demo.app.exception.UserNotEnrolledException;
import com.demo.app.model.StudentTest;
import com.demo.app.model.StudentTestDetail;
import com.demo.app.model.TestSet;
import com.demo.app.repository.*;
import com.demo.app.service.StudentTestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentTestServiceImpl implements StudentTestService {

    private static final String OFFLINE_EXAM_JSON_PATH = "data.json";

    private final ObjectMapper objectMapper;

    private final ExamClassRepository examClassRepository;

    private final StudentRepository studentRepository;

    private final StudentTestRepository studentTestRepository;

    private final TestSetRepository testSetRepository;

    private final TestSetQuestionRepository testSetQuestionRepository;

    @Override
    public void matchRandomTestForStudent(String classCode, Principal principal){
        var student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new InvalidRoleException(
                        "You don't have role to do this action!",
                        HttpStatus.FORBIDDEN));
        var examClass = examClassRepository.findByCode(classCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Class with code %s not found !", classCode),
                        HttpStatus.NOT_FOUND));
        var students = examClass.getStudents();
        if (!students.contains(student)){
            throw new UserNotEnrolledException(
                    String.format("You are not in class %s", examClass.getRoomName()),
                    HttpStatus.FORBIDDEN);
        }
        var testSets = examClass.getTest().getTestSets();
        var studentTest = StudentTest.builder()
                .student(student)
                .testSet(getRandomTestSet(testSets))
                .build();
        studentTestRepository.save(studentTest);
    }
    private TestSet getRandomTestSet(List<TestSet> testSets){
        var random = new Random();
        var size = testSets.size();
        return testSets.get(random.nextInt(size));
    }

    @Override
    @Transactional
    public void markingOfflineAnswer() throws IOException{
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
        String studentCode = "20" + offlineExam.getStudentCode();
        var student = studentRepository.findByCode(studentCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Student %s not found", offlineExam.getStudentCode()),
                        HttpStatus.NOT_FOUND));
        var questionAnswers = new HashMap<Integer, String>();
        testset.getTestSetQuestions().forEach(question -> questionAnswers.put(question.getQuestionNo(), question.getBinaryAnswer()));
        var mark = markStudentTest(offlineExam.getAnswers(), questionAnswers);
        var studentTest = StudentTest.builder()
                .student(student)
                .testSet(testset)
                .mark(mark)
                .grade((double) mark / questionAnswers.size())
                .build();
        var studentTestDetails = new ArrayList<StudentTestDetail>();
        offlineExam.getAnswers().forEach(offlineAnswer -> {
            var testSetQuestion = testSetQuestionRepository.findByTestSetAndQuestionNo(testset, offlineAnswer.getQuestionNo());
            var studentTestDetail = StudentTestDetail.builder()
                    .studentTest(studentTest)
                    .selectedAnswer(offlineAnswer.getSelected())
                    .testSetQuestion(testSetQuestion)
                    .build();
            studentTestDetails.add(studentTestDetail);
        });
        studentTest.setStudentTestDetails(studentTestDetails);
        studentTestRepository.save(studentTest);
    }

    private int markStudentTest(List<OfflineAnswer> offlineAnswers, Map<Integer, String> correctedAnswers){
        var mark = 0;
        for (var offlineAnswer : offlineAnswers){
            String corrected = correctedAnswers.get(offlineAnswer.getQuestionNo());
            if (corrected.equals(offlineAnswer.getSelected()))
                mark++;
        }
        return mark;
    }

}
