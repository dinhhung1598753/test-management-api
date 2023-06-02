package com.demo.app.repository;

import com.demo.app.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("select t from Teacher t join User u on t.user.id = u.id where u.enabled = :enabled")
    List<Teacher> findByEnabled(@Param("enabled") boolean enabled);
}

