package com.demo.app.service.impl;

import com.demo.app.dto.examClass.ClassDetailResponse;
import com.demo.app.dto.examClass.ClassRequest;
import com.demo.app.dto.examClass.ClassResponse;
import com.demo.app.dto.examClass.ClassStudentRequest;
import com.demo.app.dto.student_test.StudentTestExcelResponse;
import com.demo.app.exception.*;
import com.demo.app.model.Student;
import com.demo.app.model.StudentTest;
import com.demo.app.repository.*;
import com.demo.app.model.ExamClass;
import com.demo.app.service.ExamClassService;
import com.demo.app.util.excel.ExcelUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    private final StudentTestRepository studentTestRepository;

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
        @SuppressWarnings("DefaultLocale") var test = testRepository.findById(request.getTestId())
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
    @Transactional
    public void importClassStudents(String classCode, MultipartFile file) throws IOException {
        if (ExcelUtils.notHaveExcelFormat(file)){
            throw new FileInputException(
                    "There are something wrong with file, please check file format is .xlsx !",
                    HttpStatus.CONFLICT);
        }
        var requests = ExcelUtils.convertExcelToDataTransferObject(file, ClassStudentRequest.class);
        var examClass = examClassRepository.findByCode(classCode)
                .orElseThrow(() -> new InvalidArgumentException("Class does not existed", HttpStatus.BAD_REQUEST));
        var codes = requests.parallelStream()
                .map(ClassStudentRequest::getCode)
                .collect(Collectors.toList());
        var students = studentRepository.findByCodeIn(codes);
        examClass.getStudents().addAll(students);
        examClassRepository.save(examClass);
    }

    @Override
    public List<ClassResponse> getAllEnabledExamClass() {
        var examClasses = examClassRepository.findByEnabled(true);
        return examClasses.stream()
                .map(examClass -> mapper.map(examClass, ClassResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public ClassDetailResponse getExamClassDetail(int examClassId){
        var objects = examClassRepository.findByJoinStudentAndStudentTestWhereId(examClassId);
        var studentClasses = objects.parallelStream().map(object -> {
            var student = (Student) object[1];
            var studentTest = (StudentTest) object[2];
            return ClassDetailResponse.StudentClassResponse.builder()
                    .fullname(student.getFullname())
                    .code(student.getCode())
                    .state(studentTest.getState().toString())
                    .testDate(studentTest.getTestDate().toString())
                    .grade(studentTest.getGrade())
                    .build();
        }).collect(Collectors.toList());
        return ClassDetailResponse.builder()
                .students(studentClasses)
                .build();
    }

    @Override
    public ByteArrayInputStream exportStudentTestToExcel(String code) throws IOException {
        var examClass = examClassRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Exam class " + code + " not found !",
                        HttpStatus.NOT_FOUND));
        var studentTests = studentTestRepository.findByExamClassId(examClass.getId());
        var studentTestExcelResponses = studentTests.parallelStream()
                .map(studentTest -> {
                    var student = studentTest.getStudent();
                    return StudentTestExcelResponse.builder()
                            .examClassCode(code)
                            .testDate(studentTest.getTestDate().toString())
                            .fullName(student.getFullname())
                            .grade(studentTest.getGrade())
                            .studentCode(student.getCode())
                            .build();
                }).collect(Collectors.toList());
        return ExcelUtils.convertContentsToExcel(studentTestExcelResponses);
    }

    @Override
    public void disableExamClass(int examClassId) {
        @SuppressWarnings("DefaultLocale") var examClass = examClassRepository.findById(examClassId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exam Class with id: %d not found !", examClassId), HttpStatus.NOT_FOUND)
        );
        examClass.setEnabled(false);
        examClassRepository.save(examClass);
    }
}
