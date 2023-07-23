package com.demo.app.service.impl;

import com.demo.app.dto.examClass.*;
import com.demo.app.dto.studentTest.StudentTestExcelResponse;
import com.demo.app.exception.*;
import com.demo.app.model.State;
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
import java.time.LocalDate;
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
        if (examClassRepository.existsByCodeAndEnabledIsTrue(request.getCode())) {
            throw new FieldExistedException("Class's code already taken !", HttpStatus.CONFLICT);
        }
        var teacher = teacherRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new InvalidRoleException("You don't have role to do this action!", HttpStatus.FORBIDDEN));
        var students = studentRepository.findAllById(request.getStudentIds());
        @SuppressWarnings("DefaultLocale") var test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("test with id %d not found !", request.getTestId()), HttpStatus.NOT_FOUND));
        var examClass = mapper.map(request, ExamClass.class);

        examClass.setId(null);
        examClass.setStudents(students.parallelStream().collect(Collectors.toSet()));
        examClass.setTeacher(teacher);
        examClass.setTest(test);
        examClass.setSubject(test.getSubject());
        examClass.setEnabled(true);
        examClassRepository.save(examClass);
    }


    @Override
    @Transactional
    public ExamClass joinExamClassByCode(String classCode, Principal principal) {
        var student = studentRepository.findByUsernameAndEnabledIsTrue(principal.getName())
                .orElseThrow(() -> new InvalidRoleException("You don't have role to do this action!", HttpStatus.FORBIDDEN));
        var examClass = examClassRepository.findByCodeAndEnabledIsTrue(classCode)
                .orElseThrow(() -> new EntityNotFoundException("Class does not existed", HttpStatus.BAD_REQUEST));
        var objects = examClassRepository.findByJoinStudentAndEnabledIsTrue(classCode);
        var students = objects.parallelStream()
                .map(object -> (Student) object[1])
                .collect(Collectors.toSet());
        students.add(student);
        examClass.setStudents(students);
        return examClassRepository.save(examClass);
    }

    @Override
    @Transactional
    public void importClassStudents(String classCode, MultipartFile file) throws IOException {
        if (ExcelUtils.notHaveExcelFormat(file)) {
            throw new FileInputException(
                    "There are something wrong with file, please check file format is .xlsx !",
                    HttpStatus.CONFLICT);
        }
        var requests = ExcelUtils.convertExcelToDataTransferObject(file, ClassStudentRequest.class);
        var examClass = examClassRepository.findByCodeAndEnabledIsTrue(classCode)
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
    public ClassDetailResponse getExamClassDetail(int examClassId) {
        var objects = examClassRepository.findByJoinStudentWhereIdAndEnabledIsTrue(examClassId);
        var studentClasses = objects.parallelStream()
                .map(object -> {
                    var student = (Student) object[1];
                    var studentTest = studentTestRepository.findFirstByStudentAndExamClassIdAndEnabledIsTrueOrderByUpdatedAtDesc(student, examClassId)
                            .orElse(StudentTest.builder().state(State.NOT_ATTEMPT)
                                    .testDate(LocalDate.of(2000, 1, 1))
                                    .build());
                    return ClassDetailResponse.StudentClassResponse.builder()
                            .fullName(student.getFullname())
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
    public List<ClassResponse> getStudentExamClass(Principal principal) {
        var student = studentRepository.findByUsernameAndEnabledIsTrue(principal.getName()).get();
        var examClasses = examClassRepository.findByStudentIdAndEnabledIsTrue(student.getId());
        return examClasses.stream()
                .map(examClass -> mapper.map(examClass, ClassResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public ClassInfoResponse getExamClassInfo(Integer examClassId, Principal principal) {
        var student = studentRepository.findByUsernameAndEnabledIsTrue(principal.getName()).get();
        var examClass = examClassRepository.findById(examClassId)
                .orElseThrow(() -> new EntityNotFoundException("Exam class not found !", HttpStatus.NOT_FOUND));
        var studentTest = studentTestRepository.findFirstByStudentAndExamClassIdAndEnabledIsTrueOrderByUpdatedAtDesc(student, examClassId)
                .orElse(StudentTest.builder()
                        .state(State.NOT_ATTEMPT)
                        .build());
        var classResponse = mapper.map(examClass, ClassInfoResponse.ClassResponse.class);
        var testResponse = mapper.map(examClass.getTest(), ClassInfoResponse.TestResponse.class);
        testResponse.setState(studentTest.getState().toString());
        return ClassInfoResponse.builder()
                .examClass(classResponse)
                .test(testResponse)
                .build();
    }

    @Override
    public ByteArrayInputStream exportStudentTestToExcel(String code) throws IOException {
        var examClass = examClassRepository.findByCodeAndEnabledIsTrue(code)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Exam class " + code + " not found !",
                        HttpStatus.NOT_FOUND));
        var examClassStudents = examClassRepository.findByJoinStudentWhereIdAndEnabledIsTrue(examClass.getId());
        var studentTestExcelResponses = examClassStudents.parallelStream()
                .map(examClassStudent -> {
                    var student = (Student) examClassStudent[1];
                    var studentTest = studentTestRepository.findFirstByStudentAndExamClassIdAndEnabledIsTrueOrderByUpdatedAtDesc(student, examClass.getId())
                            .orElse(StudentTest.builder().state(State.NOT_ATTEMPT)
                                    .testDate(LocalDate.of(2000, 1, 1))
                                    .build());
                    return StudentTestExcelResponse.builder()
                            .classCode(code)
                            .testDate(studentTest.getTestDate().toString())
                            .fullName(student.getFullname())
                            .grade(studentTest.getGrade())
                            .studentCode(student.getCode())
                            .state(studentTest.getState().toString())
                            .course(student.getCourse())
                            .email(student.getUser().getEmail())
                            .build();
                }).collect(Collectors.toList());
        if (studentTestExcelResponses.size() == 0){
            studentTestExcelResponses.add(StudentTestExcelResponse.builder().build());
        }
        return ExcelUtils.convertContentsToExcel(studentTestExcelResponses);
    }

    @Override
    public void disableExamClass(int examClassId) {
         var examClass = examClassRepository.findById(examClassId).orElseThrow(
                () -> new EntityNotFoundException("Exam class not found !", HttpStatus.NOT_FOUND)
        );
        examClass.setEnabled(false);
        examClass.setStudents(null);
        examClassRepository.save(examClass);
    }
}
