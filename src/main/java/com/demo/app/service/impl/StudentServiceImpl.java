package com.demo.app.service.impl;

import com.demo.app.config.security.PasswordEncoder;
import com.demo.app.dto.student.StudentRequest;
import com.demo.app.dto.student.StudentResponse;
import com.demo.app.dto.student.StudentSearchRequest;
import com.demo.app.dto.student.StudentUpdateRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.DuplicatedUniqueValueException;
import com.demo.app.exception.FileInputException;
import com.demo.app.model.Gender;
import com.demo.app.model.Role;
import com.demo.app.model.Student;
import com.demo.app.model.User;
import com.demo.app.repository.RoleRepository;
import com.demo.app.repository.StudentRepository;
import com.demo.app.repository.UserRepository;
import com.demo.app.service.StudentService;
import com.demo.app.specification.EntitySpecification;
import com.demo.app.specification.SearchFilter;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public void importStudentExcel(MultipartFile file) throws DuplicatedUniqueValueException, IOException {
        if (ExcelUtils.notHaveExcelFormat(file)){
            throw new FileInputException(
                    "There are something wrong with file, please check file format is .xlsx !",
                    HttpStatus.CONFLICT);
        }
        var requests = ExcelUtils.convertExcelToDataTransferObject(file, StudentRequest.class);
        var users = requests.parallelStream()
                .map(this::mapRequestToUser)
                .collect(Collectors.toList());
        userRepository.saveAll(users);
    }

    @Override
    @Transactional
    public void saveStudent(StudentRequest request) throws DuplicatedUniqueValueException {
        userRepository.save(mapRequestToUser(request));
    }

    private User mapRequestToUser(StudentRequest request){
        checkIfUsernameExists(request.getUsername());
        checkIfEmailExists(request.getEmail());
        checkIfPhoneNumberExists(request.getPhoneNumber());
        checkIfCodeExists(request.getCode());
        var roles = roleRepository.findAllByRoleNameIn(
                List.of(Role.RoleType.ROLE_USER, Role.RoleType.ROLE_STUDENT)
        );
        User user = mapper.map(request, User.class);
        user.setPassword(passwordEncoder.passwordEncode().encode(request.getPassword()));
        user.setRoles(roles);
        user.getStudent().setUser(user);
        return user;
    }

    @Override
    public ByteArrayInputStream exportStudentsToExcel() throws IOException {
        var students = studentRepository.findByEnabled(true);
        var responses = mapStudentToResponse(students);
        return ExcelUtils.convertContentsToExcel(responses);
    }

    @Override
    public List<StudentResponse> getAllStudents() throws EntityNotFoundException {
        var students = studentRepository.findByEnabled(true);
        return mapStudentToResponse(students);
    }

    @Override
    public List<StudentResponse> searchByFilter(StudentSearchRequest request) {
        var filter = mapper.map(request, SearchFilter.class);
        var students = studentRepository.findAll(new EntitySpecification<Student>().withFilters(filter));
        return mapStudentToResponse(students);
    }

    private List<StudentResponse> mapStudentToResponse(List<Student> students){
        return students.parallelStream().map(student -> {
            var response = mapper.map(student, StudentResponse.class);
            response.setUsername(student.getUser().getUsername());
            response.setEmail(student.getUser().getEmail());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStudentById(int studentId, StudentUpdateRequest request) throws EntityNotFoundException, DuplicatedUniqueValueException {
        var existStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Student with id: %s not found !", studentId), HttpStatus.NOT_FOUND));
        updateStudent(existStudent, request);
    }

    @Override
    @Transactional
    public void updateStudentProfile(Principal principal, StudentUpdateRequest request) throws EntityNotFoundException, DuplicatedUniqueValueException {
        var student = studentRepository.findByUsernameAndEnabledIsTrue(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Student with username: %s not found !", principal.getName()),
                        HttpStatus.NOT_FOUND));
        updateStudent(student, request);
    }

    private void updateStudent(Student student, StudentUpdateRequest request){
        if (!student.getPhoneNumber().equals(request.getPhoneNumber()))
            checkIfPhoneNumberExists(request.getPhoneNumber());
        if (!student.getUser().getEmail().equals(request.getEmail()))
            checkIfEmailExists(request.getEmail());

        student.setFullname(request.getFullName());
        student.setPhoneNumber(request.getPhoneNumber());
        student.getUser().setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        student.setBirthday(LocalDate.parse(
                request.getBirthday(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ));
        student.setGender(Gender.valueOf(request.getGender()));

        studentRepository.save(student);
    }

    @Override
    public void disableStudent(int studentId) throws EntityNotFoundException {
        var existStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found", HttpStatus.NOT_FOUND));
        existStudent.getUser().setEnabled(false);
        studentRepository.save(existStudent);
    }

    private void checkIfUsernameExists(String username) throws DuplicatedUniqueValueException {
        if (userRepository.existsByUsernameAndEnabledIsTrue(username)) {
            throw new DuplicatedUniqueValueException("Username already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfPhoneNumberExists(String phoneNumber) throws DuplicatedUniqueValueException {
        if (studentRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicatedUniqueValueException("Phone number already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfEmailExists(String email) throws DuplicatedUniqueValueException {
        if (userRepository.existsByEmailAndEnabledTrue(email)) {
            throw new DuplicatedUniqueValueException("Email already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfCodeExists(String code) throws DuplicatedUniqueValueException {
        if (studentRepository.existsByCode(code)) {
            throw new DuplicatedUniqueValueException("Code already taken!", HttpStatus.CONFLICT);
        }
    }
}
