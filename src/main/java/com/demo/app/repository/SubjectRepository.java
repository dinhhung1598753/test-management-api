package com.demo.app.repository;

import com.demo.app.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    Boolean existsByCode(String code);

    Optional<Subject> findByCode(String code);
}
