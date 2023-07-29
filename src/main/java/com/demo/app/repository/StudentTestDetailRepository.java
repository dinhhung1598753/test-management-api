package com.demo.app.repository;

import com.demo.app.model.StudentTest;
import com.demo.app.model.StudentTestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentTestDetailRepository extends JpaRepository<StudentTestDetail, Integer> {

    List<StudentTestDetail> findByStudentTest(StudentTest studentTest);

}
