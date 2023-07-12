package com.demo.app.repository;

import com.demo.app.model.Test;
import com.demo.app.model.TestSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSetRepository extends JpaRepository<TestSet, Integer> {

    Boolean existsByTestAndTestNoAndEnabledTrue(Test test, int testNo);

    Optional<TestSet> findByTestAndTestNoAndEnabledTrue(Test test, int testNo);

    List<TestSet> findByEnabledIsTrueAndTest(Test test);

    Optional<TestSet> findByTestIdAndTestNo(Integer testId, Integer testNo);

    @Query("""
            select ts
            from Test t
                inner join TestSet ts on t.id = ts.test.id
            where t.id = :test_id
            order by rand()
            """)
    Page<TestSet> findRandomTestSetByTest(@Param("test_id") Integer testId, Pageable pageable);

}
