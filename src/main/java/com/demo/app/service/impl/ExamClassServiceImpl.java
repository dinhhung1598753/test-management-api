package com.demo.app.service.impl;

import com.demo.app.dto.examClass.ClassRequest;
import com.demo.app.dto.examClass.ClassResponse;
import com.demo.app.exception.*;
import com.demo.app.repository.*;
import com.demo.app.model.ExamClass;
import com.demo.app.service.ExamClassService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamClassServiceImpl implements ExamClassService {

    private final ExamClassRepository examClassRepository;

    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    private final TestRepository testRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public void createExamClass(ClassRequest request, Principal principal) {
        if (examClassRepository.existsByCode(request.getCode())) {
            throw new FieldExistedException("Class's code already taken !", HttpStatus.CONFLICT);
        }
        var teacher = teacherRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new InvalidRoleException("You don't have role to do this action!", HttpStatus.FORBIDDEN));
        var students = studentRepository.findAllById(request.getStudentIds());
        var test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("test with id %d not found !", request.getTestId()), HttpStatus.NOT_FOUND));
        var examClass = mapper.map(request, ExamClass.class);

        examClass.setId(null);
        examClass.setStudents(students);
        examClass.setTeacher(teacher);
        examClass.setTest(test);
        examClass.setSubject(test.getSubject());
        examClass.setEnabled(true);
        examClassRepository.save(examClass);
    }


    @Override
    @Transactional
    public ExamClass joinExamClassByCode(String classCode, Principal principal){
        var student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new InvalidRoleException("You don't have role to do this action!", HttpStatus.FORBIDDEN));
        var examClass = examClassRepository.findByCode(classCode)
                .orElseThrow(() -> new InvalidArgumentException("Class does not existed", HttpStatus.BAD_REQUEST));
        if(examClass.getStudents() == null)
            examClass.setStudents(new ArrayList<>());
        examClass.getStudents().add(student);
        return examClassRepository.save(examClass);
    }

    @Override
    public List<ClassResponse> getAllEnabledExamClass() {
        var examClasses = examClassRepository.findByEnabled(true);
        return examClasses.stream()
                .map(examClass -> mapper.map(examClass, ClassResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void disableExamClass(int examClassId) {
        var examClass = examClassRepository.findById(examClassId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exam Class with id: %d not found !", examClassId), HttpStatus.NOT_FOUND)
        );
        examClass.setEnabled(false);
        examClassRepository.save(examClass);
    }
}
