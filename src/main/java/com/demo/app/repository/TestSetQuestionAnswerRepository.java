package com.demo.app.repository;

import com.demo.app.model.TestSetQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestSetQuestionAnswerRepository extends JpaRepository<TestSetQuestionAnswer, Integer> {
}
