package com.demo.app.service.impl;

import com.demo.app.config.security.PasswordEncoder;
import com.demo.app.dto.teacher.TeacherRequest;
import com.demo.app.dto.teacher.TeacherResponse;
import com.demo.app.dto.teacher.TeacherUpdateRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import com.demo.app.model.Gender;
import com.demo.app.model.Role;
import com.demo.app.model.Teacher;
import com.demo.app.model.User;
import com.demo.app.repository.RoleRepository;
import com.demo.app.repository.TeacherRepository;
import com.demo.app.repository.UserRepository;
import com.demo.app.service.TeacherService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public void saveTeacher(TeacherRequest request) throws FieldExistedException {
        checkIfUsernameExists(request.getUsername());
        checkIfEmailExists(request.getEmail());
        checkIfPhoneNumberExists(request.getPhoneNumber());
        checkIfCodeExists(request.getCode());

        var roles = roleRepository.findAllByRoleNameIn(Arrays.asList(
                Role.RoleType.ROLE_USER,
                Role.RoleType.ROLE_TEACHER));
        var user = mapper.map(request, User.class);
        String encodePassword = passwordEncoder.passwordEncode().encode(request.getPassword());

        user.setPassword(encodePassword);
        user.setRoles(roles);
        user.setEnabled(true);
        user.getTeacher().setUser(user);
        userRepository.save(user);
    }

    @Override
    public List<TeacherResponse> getAllTeacher() {
        List<Teacher> teachers = teacherRepository.findByEnabled(true);
        return teachers.parallelStream()
                .map(teacher -> {
                    var response = mapper.map(teacher, TeacherResponse.class);
                    var user = teacher.getUser();
                    response.setUsername(user.getUsername());
                    response.setEmail(user.getEmail());
                    return response;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateTeacher(int teacherId, TeacherUpdateRequest request) throws EntityNotFoundException, FieldExistedException {
        var teacher = teacherRepository.findById(teacherId).
                orElseThrow(() -> new EntityNotFoundException(String.format("Teacher with id %d not found !", teacherId), HttpStatus.NOT_FOUND));
        if (!teacher.getPhoneNumber().equals(request.getPhoneNumber())) {
            checkIfPhoneNumberExists(request.getPhoneNumber());
        }
        if (!teacher.getUser().getEmail().equals(request.getEmail())) {
            checkIfEmailExists(request.getEmail());
        }
        if (!teacher.getCode().equals(request.getCode())) {
            checkIfCodeExists(request.getCode());
        }
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        teacher.setBirthday(LocalDate.parse(request.getBirthday(), formatter));
        teacher.setCode(request.getCode());
        teacher.setPhoneNumber(request.getPhoneNumber());
        teacher.setFullname(request.getFullName());
        teacher.getUser().setEmail(request.getEmail());
        teacher.setGender(Gender.valueOf(request.getGender()));
        teacherRepository.save(teacher);
    }

    @Override
    public void disableTeacher(int teacherId) throws EntityNotFoundException {
        var existTeacher = teacherRepository.findById(teacherId).
                orElseThrow(() -> new EntityNotFoundException(String.format("Not found any teacher with id = %d", teacherId), HttpStatus.NOT_FOUND));
        existTeacher.getUser().setEnabled(false);
        teacherRepository.save(existTeacher);
    }

    private void checkIfUsernameExists(String username) throws FieldExistedException {
        if (userRepository.existsByUsername(username)) {
            throw new FieldExistedException("Username already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfPhoneNumberExists(String phoneNumber) throws FieldExistedException {
        if (teacherRepository.existsByPhoneNumber(phoneNumber)) {
            throw new FieldExistedException("Phone number already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfEmailExists(String email) throws FieldExistedException {
        if (userRepository.existsByEmailAndEnabledTrue(email)) {
            throw new FieldExistedException("Email already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfCodeExists(String code) throws FieldExistedException {
        if (teacherRepository.existsByCode(code)) {
            throw new FieldExistedException("Code already taken!", HttpStatus.CONFLICT);
        }
    }
}
