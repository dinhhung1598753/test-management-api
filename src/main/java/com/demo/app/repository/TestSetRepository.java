package com.demo.app.repository;

import com.demo.app.model.Test;
import com.demo.app.model.TestSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSetRepository extends JpaRepository<TestSet, Integer> {

    Boolean existsByTestAndTestNoAndEnabledTrue(Test test, int testNo);

    List<TestSet> findByEnabledIsTrue();

    Optional<TestSet> findByTestAndTestNoAndEnabledTrue(Test test, int testNo);
}
