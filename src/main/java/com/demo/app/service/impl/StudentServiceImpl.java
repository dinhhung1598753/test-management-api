package com.demo.app.service.impl;

import com.demo.app.config.security.PasswordEncoder;
import com.demo.app.dto.student.StudentRequest;
import com.demo.app.dto.student.StudentResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import com.demo.app.exception.FileInputException;
import com.demo.app.model.Role;
import com.demo.app.model.Student;
import com.demo.app.model.User;
import com.demo.app.repository.RoleRepository;
import com.demo.app.repository.StudentRepository;
import com.demo.app.repository.UserRepository;
import com.demo.app.service.StudentService;
import com.demo.app.util.ExcelUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public void saveStudentsExcelFile(MultipartFile file) throws FileInputException, FieldExistedException {
        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new FileInputException("Please upload an excel file!", HttpStatus.BAD_REQUEST);
        }
        try {
            Map<User, Student> userStudents = ExcelUtils.excelFileToUserStudents(file);
            List<Role> roles = roleRepository.findAllByRoleNameIn(Arrays.asList(Role.RoleType.ROLE_USER, Role.RoleType.ROLE_STUDENT));
            userStudents.forEach((user, student) -> {
                if (userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername()) ||
                        studentRepository.existsByPhoneNumber(student.getPhoneNumber()) ||
                        studentRepository.existsByCode(student.getCode())) {
                    throw new FieldExistedException("Some field in excel file already existed !", HttpStatus.CONFLICT);
                }
                user.setRoles(roles);
                String encodePassword = passwordEncoder.passwordEncode().encode(user.getPassword());
                user.setPassword(encodePassword);
                student.setUser(user);
            });
            userRepository.saveAll(userStudents.keySet());
            studentRepository.saveAll(userStudents.values());
        } catch (IOException ex) {
            throw new FileInputException("Could not read the file !", HttpStatus.EXPECTATION_FAILED);
        }
    }

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
    public void saveStudent(StudentRequest request) throws FieldExistedException {
        checkIfUsernameExists(request.getUsername());
        checkIfEmailExists(request.getEmail());
        checkIfPhoneNumberExists(request.getPhoneNumber());
        checkIfCodeExists(request.getCode());

        List<Role> roles = roleRepository.findAllByRoleNameIn(Arrays.asList(Role.RoleType.ROLE_USER, Role.RoleType.ROLE_STUDENT));
        User user = mapper.map(request, User.class);
        user.setPassword(passwordEncoder.passwordEncode().encode(request.getPassword()));
        user.setRoles(roles);
        user.getStudent().setUser(user);
        user.setEnabled(true);
        userRepository.save(user);
    }


    @Override
    public List<StudentResponse> getAllStudents() throws EntityNotFoundException {
        List<Student> students = studentRepository.findByEnabled(true);
        if (students.size() == 0) {
            throw new EntityNotFoundException("Not found any students", HttpStatus.NOT_FOUND);
        }
        return students.stream().map(student -> {
            StudentResponse response = mapper.map(student, StudentResponse.class);
            response.setUsername(student.getUser().getUsername());
            response.setEmail(student.getUser().getEmail());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStudent(int studentId, StudentRequest request) throws EntityNotFoundException, FieldExistedException {
        Student existStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Student %s not found !", request.getUsername()), HttpStatus.NOT_FOUND));
        if (!existStudent.getUser().getUsername().equals(request.getUsername())) {
            checkIfUsernameExists(request.getUsername());
        }
        if (!existStudent.getPhoneNumber().equals(request.getPhoneNumber())) {
            checkIfPhoneNumberExists(request.getPhoneNumber());
        }
        if (!existStudent.getUser().getEmail().equals(request.getEmail())) {
            checkIfEmailExists(request.getEmail());
        }
        if (!existStudent.getCode().equals(request.getCode())) {
            checkIfCodeExists(request.getCode());
        }

        var student = mapper.map(request, Student.class);
        student.setJoinDate(existStudent.getJoinDate());
        student.setUser(existStudent.getUser());
        student.setId(existStudent.getId());
        var user = student.getUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.passwordEncode().encode(request.getPassword()));
        studentRepository.save(student);
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
        if (userRepository.existsByEmail(email)) {
            throw new FieldExistedException("Email already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfCodeExists(String code) throws FieldExistedException {
        if (studentRepository.existsByCode(code)) {
            throw new FieldExistedException("Code already taken!", HttpStatus.CONFLICT);
        }
    }

}
