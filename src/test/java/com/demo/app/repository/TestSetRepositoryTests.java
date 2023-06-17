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
public class TestSetRepositoryTests {

    @Autowired
    private TestSetRepository testSetRepository;

    private static final int EXPECT_TEST_ID = 16;

    private static final int EXPECT_TEST_SET_ID = 101;

    @Test
    @Ignore
    public void testExistByTestAndTestNo(){
        var test = new com.demo.app.model.Test();
        test.setId(EXPECT_TEST_ID);
        var flag = testSetRepository.existsByTestAndTestNoAndEnabledTrue(test, EXPECT_TEST_SET_ID);
        Assertions.assertThat(flag).isTrue();
    }

    @Test
    public void testFindByTestAndTestNoAndEnabledTrue(){
        var test = new com.demo.app.model.Test();
        test.setId(EXPECT_TEST_ID);
        var testset = testSetRepository.findByTestAndTestNoAndEnabledTrue(test, EXPECT_TEST_SET_ID).orElse(null);
        Assertions.assertThat(testset).isNotNull();
    }
}
