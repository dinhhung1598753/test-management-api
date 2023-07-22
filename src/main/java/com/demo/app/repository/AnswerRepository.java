package com.demo.app.repository;

import com.demo.app.model.Answer;
import com.demo.app.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    List<Answer> findByQuestionAndEnabledIsTrue(Question question);

}
