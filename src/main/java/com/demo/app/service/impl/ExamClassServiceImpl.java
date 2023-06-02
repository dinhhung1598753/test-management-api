package com.demo.app.service.impl;

import com.demo.app.dto.examClass.ClassRequest;
import com.demo.app.dto.examClass.ClassResponse;
import com.demo.app.dto.student.StudentResponse;
import com.demo.app.dto.subject.SubjectResponse;
import com.demo.app.dto.teacher.TeacherResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import com.demo.app.model.ExamClass;
import com.demo.app.repository.ExamClassRepository;
import com.demo.app.repository.StudentRepository;
import com.demo.app.repository.SubjectRepository;
import com.demo.app.repository.TeacherRepository;
import com.demo.app.service.ExamClassService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamClassServiceImpl implements ExamClassService {

    private final ExamClassRepository examClassRepository;

    private final StudentRepository studentRepository;

    private final TeacherRepository teacherRepository;

    private final SubjectRepository subjectRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public void createExamClass(ClassRequest request) {
        if(examClassRepository.existsByCode(request.getCode())){
            throw new FieldExistedException("Class's code already taken !", HttpStatus.CONFLICT);
        }

        var examClass = mapper.map(request, ExamClass.class);
        examClassRepository.save(examClass);
    }

    @Override
    @Transactional
    public void addStudentsToExamClass(int examClassId, List<Integer> studentIds){
        var examClass = examClassRepository.findById(examClassId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exam Class with id: %d not found !", examClassId), HttpStatus.NOT_FOUND)
        );
        var students = studentIds.stream().map(
                studentId -> studentRepository.findById(studentId).get()
        ).collect(Collectors.toSet());
        examClass.setStudents(students);
        examClassRepository.save(examClass);
    }

    @Override
    public void addTeacherToExamClass(int examClassId, int teacherId){
        var examClass = examClassRepository.findById(examClassId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exam Class with id: %d not found !", examClassId), HttpStatus.NOT_FOUND)
        );
        var teacher = teacherRepository.findById(teacherId).get();
        examClass.setTeacher(teacher);
        examClassRepository.save(examClass);
    }

    @Override
    public void addSubjectToExamClass(int examClassId, int subjectId){
        var examClass = examClassRepository.findById(examClassId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exam Class with id: %d not found !", examClassId), HttpStatus.NOT_FOUND)
        );
        var subject = subjectRepository.findById(subjectId).get();
        examClass.setSubject(subject);
        examClassRepository.save(examClass);
    }

    @Override
    public List<ClassResponse> getAllEnabledExamClass(){
        var examClasses = examClassRepository.findByEnabled(true);
        if (examClasses.size() == 0){
            throw new EntityNotFoundException("Not found any class !!", HttpStatus.NOT_FOUND);
        }
        return examClasses.stream().map(examClass -> {
            var classResponse = mapper.map(examClass, ClassResponse.class);
            var studentResponses = examClass.getStudents().stream().map(
                    student -> mapper.map(student, StudentResponse.class)
            ).collect(Collectors.toList());
            var teacherResponse = mapper.map(examClass.getTeacher(), TeacherResponse.class);
            var subjectResponse = mapper.map(examClass.getSubject(), SubjectResponse.class);

            classResponse.setStudentResponses(studentResponses);
            classResponse.setTeacherResponse(teacherResponse);
            classResponse.setSubjectResponse(subjectResponse);
            return classResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public void disableExamClass(int examClassId){
        var examClass = examClassRepository.findById(examClassId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exam Class with id: %d not found !", examClassId), HttpStatus.NOT_FOUND)
        );
        examClass.setEnabled(false);
        examClassRepository.save(examClass);
    }
}
