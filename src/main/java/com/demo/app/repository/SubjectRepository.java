package com.demo.app.repository;

import com.demo.app.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    Boolean existsByCodeAndEnabledTrue(String code);

    Optional<Subject> findByCode(String code);

    List<Subject> findByEnabledIsTrue();
}
