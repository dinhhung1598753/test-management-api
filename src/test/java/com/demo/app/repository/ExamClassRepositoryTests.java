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

import java.util.List;


@DataJpaTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ProjectDesignIApplication.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ExamClassRepositoryTests {

    @Autowired
    private ExamClassRepository examClassRepository;

    @Test
    public void testFindByCode(){
        String code = "34422";
        var examClass = examClassRepository.findByCode(code);
        Assertions.assertThat(examClass).isNotNull();
    }

    @Test
    public void test(){
        int examClassId = 2;
        List<Object[]> objects = examClassRepository.findByJoinStudentAndStudentTestWhereId(examClassId);
        objects.forEach(object -> {
            System.out.println(object[0]);
            System.out.println(object[1]);
            System.out.println(object[2]);
        });
    }

}
