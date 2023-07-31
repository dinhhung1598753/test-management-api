package com.demo.app.repository;

import com.demo.app.model.ExamClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamClassRepository extends JpaRepository<ExamClass, Integer> {

    boolean existsByCodeAndEnabledIsTrue(String code);

    List<ExamClass> findByEnabled(Boolean enabled);

    Optional<ExamClass> findByCodeAndEnabledIsTrue(String classCode);

    @Query("""
            select ec
            from ExamClass ec
            join fetch ec.students s
            where s.id = :studentId and ec.enabled = true
            """)
    List<ExamClass> findByStudentIdAndEnabledIsTrue(@Param("studentId") Integer studentId);

    @Query("""
        select ec, s
        from ExamClass ec
        join fetch ec.students s
        where ec.code = :exam_class_code and s.enabled = true
    """)
    List<Object[]> findStudentsByCodeAndEnabledIsTrue(@Param("exam_class_code") String classCode);

    @Query("""
        select ec, s
        from ExamClass ec
        join fetch ec.students s
        where ec.id = :exam_class_id and ec.enabled = true
    """)
    List<Object[]> findStudentsByIdAndEnabledIsTrue(@Param("exam_class_id") Integer examClassId);
}
