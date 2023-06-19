package com.demo.app.service.impl;

import com.demo.app.config.security.PasswordEncoder;
import com.demo.app.dto.student.StudentRequest;
import com.demo.app.dto.student.StudentResponse;
import com.demo.app.dto.student.StudentSearchRequest;
import com.demo.app.dto.student.StudentUpdateRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
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
    public ByteArrayInputStream exportStudentsExcel() throws FileInputException {
        try {
            var students = studentRepository.findAll();
            return ExcelUtils.studentsToExcelFile(students);
        } catch (IOException ex) {
            throw new FileInputException("Could not write the file !", HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void importStudentExcel(MultipartFile file) throws FieldExistedException, IOException {
        if (!ExcelUtils.hasExcelFormat(file)){
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
    public void saveStudent(StudentRequest request) throws FieldExistedException {
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


    @Override
    @Transactional
    public void updateStudent(int studentId, StudentUpdateRequest request) throws EntityNotFoundException, FieldExistedException {
        Student existStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Student with id: %s not found !", studentId), HttpStatus.NOT_FOUND));
        if (!existStudent.getPhoneNumber().equals(request.getPhoneNumber()))
            checkIfPhoneNumberExists(request.getPhoneNumber());
        if (!existStudent.getUser().getEmail().equals(request.getEmail()))
            checkIfEmailExists(request.getEmail());
        if (!existStudent.getCode().equals(request.getCode()))
            checkIfCodeExists(request.getCode());

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        existStudent.setFullname(request.getFullName());
        existStudent.setPhoneNumber(request.getPhoneNumber());
        existStudent.setCode(request.getCode());
        existStudent.getUser().setEmail(request.getEmail());
        existStudent.setCourse(request.getCourse());
        existStudent.setBirthday(LocalDate.parse(request.getBirthday(), formatter));
        existStudent.setGender(Gender.valueOf(request.getGender()));

        studentRepository.save(existStudent);
    }

    @Override
    public void disableStudent(int studentId) throws EntityNotFoundException {
        var existStudent = studentRepository.findById(studentId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Not found any student with id = %d !", studentId), HttpStatus.NOT_FOUND)
        );
        existStudent.getUser().setEnabled(false);
        studentRepository.save(existStudent);
    }

    private void checkIfUsernameExists(String username) throws FieldExistedException {
        if (userRepository.existsByUsername(username)) {
            throw new FieldExistedException("Username already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfPhoneNumberExists(String phoneNumber) throws FieldExistedException {
        if (studentRepository.existsByPhoneNumber(phoneNumber)) {
            throw new FieldExistedException("Phone number already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfEmailExists(String email) throws FieldExistedException {
        if (userRepository.existsByEmailAndEnabledTrue(email)) {
            throw new FieldExistedException("Email already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfCodeExists(String code) throws FieldExistedException {
        if (studentRepository.existsByCode(code)) {
            throw new FieldExistedException("Code already taken!", HttpStatus.CONFLICT);
        }
    }

    private List<StudentResponse> mapStudentToResponse(List<Student> students){
        return students.parallelStream().map(student -> {
            var response = mapper.map(student, StudentResponse.class);
            response.setUsername(student.getUser().getUsername());
            response.setEmail(student.getUser().getEmail());
            return response;
        }).collect(Collectors.toList());
    }

}
