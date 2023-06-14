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
public class TestSetRepositoryTests {

    @Autowired
    private TestSetRepository testSetRepository;

    @Test
    public void testExistByTestAndTestNo(){
        var test = com.demo.app.model.Test.builder()
                .id(1)
                .build();
        var testNo = 101;
        var flag = testSetRepository.existsByTestAndTestNoAndEnabledTrue(test, testNo);
        Assertions.assertThat(flag).isTrue();
    }
}
