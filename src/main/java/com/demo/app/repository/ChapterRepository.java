package com.demo.app.repository;

import com.demo.app.model.Chapter;
import com.demo.app.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    List<Chapter> findBySubjectIdAndEnabledTrue(int subjectId);

    Boolean existsBySubjectIdAndOrderAndEnabledTrue(int subjectId, int order);

    Optional<Chapter> findBySubjectAndOrder(Subject subject, int order);

    @Query("select c from Chapter c where c.subject = :subject and c.enabled = true")
    List<Chapter> findBySubjectAndEnabledIsTrue(@Param("subject") Subject subject);
}
