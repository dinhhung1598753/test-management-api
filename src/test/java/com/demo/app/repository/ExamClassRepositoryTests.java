package com.demo.app.repository;

import com.demo.app.ProjectDesignIApplication;
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
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ExamClassRepositoryTests {

    @Autowired
    private ExamClassRepository examClassRepository;

    @Test
    public void testFindByCode(){
        String code = "200789";
        var examClass = examClassRepository.findStudentsByCodeAndEnabledIsTrue(code);

        Assertions.assertThat(examClass).isNotNull();
    }

    @Test
    public void test2(){
        Integer id = 2;
        var objects = examClassRepository.findStudentsByIdAndEnabledIsTrue(id);
        Assertions.assertThat(objects).isNotNull();
        System.out.println(objects);
    }

}
