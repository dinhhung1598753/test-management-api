package com.demo.app.repository;

import com.demo.app.model.ExamClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamClassRepository extends JpaRepository<ExamClass, Integer> {

    boolean existsByCode(String code);

    List<ExamClass> findByEnabled(Boolean enabled);

}
