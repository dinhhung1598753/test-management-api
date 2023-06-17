package com.demo.app.repository;

import com.demo.app.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Boolean existsByPhoneNumber(String phoneNumber);

    Boolean existsByCode(String code);

    Optional<Student> findByCode(String code);

    @Query("select s from Student s join User u on s.user.id = u.id where u.enabled = :enabled")
    List<Student> findByEnabled(@Param("enabled") Boolean enabled);

    @Query("select s from Student s join User u on s.user.id = u.id where u.username = :username")
    Optional<Student> findByUsername(String username);

}
