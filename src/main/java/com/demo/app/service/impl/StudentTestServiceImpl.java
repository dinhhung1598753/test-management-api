package com.demo.app.service.impl;

import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.InvalidRoleException;
import com.demo.app.exception.UserNotEnrolledException;
import com.demo.app.model.StudentTest;
import com.demo.app.model.TestSet;
import com.demo.app.repository.ExamClassRepository;
import com.demo.app.repository.StudentRepository;
import com.demo.app.repository.TestSetRepository;
import com.demo.app.service.StudentTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StudentTestServiceImpl implements StudentTestService {

    private final ExamClassRepository examClassRepository;

    private final TestSetRepository testSetRepository;

    private final StudentRepository studentRepository;

    @Override
    public void matchRandomTestForStudent(String classCode, Principal principal){
        var student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new InvalidRoleException("You don't have role to do this action!", HttpStatus.FORBIDDEN));
        var examClass = examClassRepository.findByCode(classCode)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Class with code %s not found !", classCode), HttpStatus.NOT_FOUND));
        var students = examClass.getStudents();
        if (!students.contains(student)){
            throw new UserNotEnrolledException(String.format("You are not in class %s", examClass.getRoomName()), HttpStatus.FORBIDDEN);
        }
        var testSets = examClass.getTest().getTestSets();
        var studentTest = StudentTest.builder()
                .student(student)
                .testSet(getRandomTestSet(testSets))
                .build();
    }
    private TestSet getRandomTestSet(List<TestSet> testSets){
        Random random = new Random();
        var size = testSets.size();
        return testSets.get(random.nextInt(size));
    }
}
