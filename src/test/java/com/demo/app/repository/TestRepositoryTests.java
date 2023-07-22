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
public class TestRepositoryTests {

    @Autowired
    private TestRepository testRepository;

    @Test
    public void testFindByEnabledIsTrue(){

        var test = testRepository.findByIdAndEnabledIsTrue(38).orElse(null);

        assert test != null;
        System.out.println(test);
        test.getQuestions().forEach(System.out::println);
        test.getQuestions().forEach(question ->
                question.getAnswers().forEach(System.out::println));
        Assertions.assertThat(test.getEnabled()).isTrue();
    }

}
