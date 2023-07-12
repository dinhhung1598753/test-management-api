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

    boolean existsByCode(String code);

    List<ExamClass> findByEnabled(Boolean enabled);

    Optional<ExamClass> findByCode(String classCode);

    @Query("""
            select ec, s, st
            from ExamClass ec
            join fetch ec.students s
            inner join StudentTest st on st.student.id = s.id
            where ec.id = :exam_class_id and st.examClassId = :exam_class_id
            """)
    List<Object[]> findByJoinStudentAndStudentTestWhereId(@Param("exam_class_id") int examClassId);

    @Query("""
        select ec, s
        from ExamClass ec
        join fetch ec.students s
        where ec.code = :exam_class_code
    """)
    List<Object[]> findByJoinStudent(@Param("exam_class_code") String classCode);

}
