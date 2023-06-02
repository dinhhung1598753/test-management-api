package com.demo.app.repository;

import com.demo.app.ProjectDesignIApplication;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@DataJpaTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ProjectDesignIApplication.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class QuestionRepositoryTests {
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void testFindQuestionBySubjectChapterOrder(){
        var code = "IT4110";
        var chapterOrders = List.of(new Integer[]{1, 2, 3, 4});
        var pageable = PageRequest.of(0, 10);
        var questions = questionRepository.findQuestionBySubjectChapterOrder(code, chapterOrders, pageable);
        Assertions.assertThat(questions).isNotNull();
        int count = 0;
        for (var question : questions){
            System.out.println(question);
            count++;
        }
        System.out.println(count);
        Assertions.assertThat(count).isEqualTo(10);
    }
}
