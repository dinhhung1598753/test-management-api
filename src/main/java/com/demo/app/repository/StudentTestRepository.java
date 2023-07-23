package com.demo.app.repository;

import com.demo.app.model.State;
import com.demo.app.model.Student;
import com.demo.app.model.StudentTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentTestRepository extends JpaRepository<StudentTest, Integer> {

    Boolean existsByStudentAndState(Student student, State state);

    Optional<StudentTest> findFirstByStudentAndExamClassIdAndEnabledIsTrueOrderByUpdatedAtDesc(
            Student student,
            Integer examClassId
    );

    StudentTest findStudentTestsByStudentAndStateAndExamClassIdAndEnabledIsTrue(
            Student student,
            State state,
            Integer examClassId
    );

}
