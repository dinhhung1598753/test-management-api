package com.demo.app.repository;

import com.demo.app.model.Test;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {
    List<Test> findByEnabledIsTrue();

    @EntityGraph(attributePaths = {"questions"})
    Optional<Test> findByIdAndEnabledIsTrue(Integer id);
}
