package com.demo.app.repository;

import com.demo.app.ProjectDesignIApplication;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
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
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTests {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @Ignore
    public void testFindByUsername(){
        String username = "TaiPhan11";
        var student = studentRepository.findByUsername(username).orElse(null);
        Assertions.assertThat(student).isNotNull();
    }

    @Test
    public void testSearchBy(){
        String keyword = "65";
        var students = studentRepository.searchBy(keyword);
        Assertions.assertThat(students).isNotNull();
        for(var student : students){
            System.out.println(student);
        }
    }

}
