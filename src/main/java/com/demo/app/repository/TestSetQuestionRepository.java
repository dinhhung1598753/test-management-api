package com.demo.app.repository;

import com.demo.app.model.TestSet;
import com.demo.app.model.TestSetQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestSetQuestionRepository extends JpaRepository<TestSetQuestion, Integer> {

    TestSetQuestion findByTestSetAndQuestionNo(TestSet testset, int questionNo);

}
