package com.demo.app.repository;

import com.demo.app.model.Chapter;
import com.demo.app.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    List<Chapter> findBySubjectIdAndEnabledTrue(int subjectId);

    Boolean existsBySubjectIdAndOrderAndEnabledTrue(int subjectId, int order);

    Optional<Chapter> findBySubjectAndOrder(Subject subject, int order);

}
