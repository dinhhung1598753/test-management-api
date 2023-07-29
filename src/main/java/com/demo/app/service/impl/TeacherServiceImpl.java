package com.demo.app.service.impl;

import com.demo.app.config.security.PasswordEncoder;
import com.demo.app.dto.teacher.TeacherRequest;
import com.demo.app.dto.teacher.TeacherResponse;
import com.demo.app.dto.teacher.TeacherUpdateRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.DuplicatedUniqueValueException;
import com.demo.app.exception.FileInputException;
import com.demo.app.model.Gender;
import com.demo.app.model.Role;
import com.demo.app.model.Teacher;
import com.demo.app.model.User;
import com.demo.app.repository.RoleRepository;
import com.demo.app.repository.TeacherRepository;
import com.demo.app.repository.UserRepository;
import com.demo.app.service.TeacherService;
import com.demo.app.util.excel.ExcelUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
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
    public void saveTeacher(TeacherRequest request) throws DuplicatedUniqueValueException {
        userRepository.save(mapRequestToUser(request));
    }

    @Override
    public void importTeacherExcel(MultipartFile file) throws IOException {
        if (ExcelUtils.notHaveExcelFormat(file)){
            throw new FileInputException(
                    "There are something wrong with file, please check file format is .xlsx !",
                    HttpStatus.CONFLICT);
        }
        var requests = ExcelUtils.convertExcelToDataTransferObject(file, TeacherRequest.class);
        var users = requests.parallelStream()
                .map(this::mapRequestToUser)
                .collect(Collectors.toList());
        userRepository.saveAll(users);
    }

    private User mapRequestToUser(TeacherRequest request){
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
        return user;
    }

    @Override
    public List<TeacherResponse> getAllTeacher() {
        List<Teacher> teachers = teacherRepository.findByEnabled(true);
        return mapTeacherToResponse(teachers);
    }

    @Override
    public ByteArrayInputStream exportTeachersToExcel() throws IOException {
        var teachers = teacherRepository.findByEnabled(true);
        var responses = mapTeacherToResponse(teachers);
        return ExcelUtils.convertContentsToExcel(responses);
    }

    private List<TeacherResponse> mapTeacherToResponse(List<Teacher> teachers){
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
    public void updateTeacherById(int teacherId, TeacherUpdateRequest request)
            throws EntityNotFoundException, DuplicatedUniqueValueException {
         @SuppressWarnings("DefaultLocale") var teacher = teacherRepository.findById(teacherId).
                orElseThrow(() -> new EntityNotFoundException(
                        String.format("Teacher with id %d not found !", teacherId),
                        HttpStatus.NOT_FOUND));
        updateTeacher(teacher, request);
    }

    @Override
    @Transactional
    public void updateTeacherProfile(Principal principal, TeacherUpdateRequest request)  throws EntityNotFoundException, DuplicatedUniqueValueException {
        var teacher = teacherRepository.findByUsername(principal.getName())
                        .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Teacher %s not found !", principal.getName()),
                        HttpStatus.NOT_FOUND));
        updateTeacher(teacher, request);
    }

    private void updateTeacher(Teacher teacher, TeacherUpdateRequest request){
        if (!teacher.getPhoneNumber().equals(request.getPhoneNumber())) {
            checkIfPhoneNumberExists(request.getPhoneNumber());
        }
        if (!teacher.getUser().getEmail().equals(request.getEmail())) {
            checkIfEmailExists(request.getEmail());
        }

        teacher.setPhoneNumber(request.getPhoneNumber());
        teacher.setFullname(request.getFullName());
        teacher.getUser().setEmail(request.getEmail());
        teacher.setBirthday(LocalDate.parse(
                request.getBirthday(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ));
        teacher.setGender(Gender.valueOf(request.getGender()));
        teacherRepository.save(teacher);
    }

    private void checkIfUsernameExists(String username) throws DuplicatedUniqueValueException {
        if (userRepository.existsByUsernameAndEnabledIsTrue(username)) {
            throw new DuplicatedUniqueValueException("Username already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfPhoneNumberExists(String phoneNumber) throws DuplicatedUniqueValueException {
        if (teacherRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicatedUniqueValueException("Phone number already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfEmailExists(String email) throws DuplicatedUniqueValueException {
        if (userRepository.existsByEmailAndEnabledTrue(email)) {
            throw new DuplicatedUniqueValueException("Email already taken!", HttpStatus.CONFLICT);
        }
    }

    private void checkIfCodeExists(String code) throws DuplicatedUniqueValueException {
        if (teacherRepository.existsByCode(code)) {
            throw new DuplicatedUniqueValueException("Code already taken!", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void disableTeacher(int teacherId) throws EntityNotFoundException {
        @SuppressWarnings("DefaultLocale") var existTeacher = teacherRepository.findById(teacherId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Not found any teacher with id = %d", teacherId),
                                HttpStatus.NOT_FOUND));
        existTeacher.getUser().setEnabled(false);
        teacherRepository.save(existTeacher);
    }
}
