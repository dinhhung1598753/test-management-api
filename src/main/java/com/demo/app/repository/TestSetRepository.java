package com.demo.app.repository;

import com.demo.app.model.Test;
import com.demo.app.model.TestSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestSetRepository extends JpaRepository<TestSet, Integer> {

    Boolean existsByTestAndTestNo(Test test, int testNo);
}
