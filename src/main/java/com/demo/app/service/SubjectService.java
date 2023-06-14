package com.demo.app.service;

import com.demo.app.dto.chapter.ChapterRequest;
import com.demo.app.dto.chapter.ChapterResponse;
import com.demo.app.dto.subject.SubjectRequest;
import com.demo.app.dto.subject.SubjectResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SubjectService {
    void addSubject(SubjectRequest request) throws FieldExistedException;

    List<SubjectResponse> getAllSubjects() throws EntityNotFoundException;

    void updateSubject(int subjectId, SubjectRequest request) throws EntityNotFoundException;

    void disableSubject(int subjectId);

    @Transactional
    List<ChapterResponse> getAllSubjectChapters(String code) throws EntityNotFoundException;

    @Transactional
    void addSubjectChapter(String code, ChapterRequest request) throws EntityNotFoundException;

    void addSubjectChapters(String code, List<ChapterRequest> request);

    void updateSubjectChapter(int chapterId, ChapterRequest request);

    void disableChapter(int chapterId);
}
