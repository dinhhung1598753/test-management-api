package com.demo.app.repository;

import com.demo.app.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    List<Chapter> findBySubjectIdAndEnabledTrue(int subjectId);

    int countBySubjectId(int subjectId);

    Boolean existsBySubjectIdAndOrderAndEnabledTrue(int subjectId, int order);

}
