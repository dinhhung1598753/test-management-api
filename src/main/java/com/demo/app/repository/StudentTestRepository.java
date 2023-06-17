package com.demo.app.repository;

import com.demo.app.model.StudentTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTestRepository extends JpaRepository<StudentTest, Integer> {
}
