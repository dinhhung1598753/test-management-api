package com.demo.app.repository;

import com.demo.app.ProjectDesignIApplication;
import com.demo.app.model.State;
import com.demo.app.model.Student;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ProjectDesignIApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentTestRepositoryTests {

    @Autowired
    private StudentTestRepository studentTestRepository;

    @Test
    public void testExistByState() {
        Student student = new Student();
        student.setId(10);
        boolean flag = studentTestRepository.existsByStudentAndState(student, State.IN_PROGRESS);
        Assertions.assertThat(flag).isTrue();
    }

    @Test
    public void testFindStudentTestsByStudentAndState(){
        var student = new Student();
        student.setId(1);
        var studentTest = studentTestRepository.findStudentTestsByStudentAndStateAndExamClassId(student, State.IN_PROGRESS, 2);
        Assertions.assertThat(studentTest).isNotNull();
        System.out.println(studentTest);
    }

}
