package com.demo.app.service;

import com.demo.app.model.Student;
import com.demo.app.repository.StudentRepository;
import com.demo.app.service.impl.StudentServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTests {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    public void StudentService_CreateStudent_ReturnStudentResponse(){
        var student = Student.builder()
                .build();
    }
}
