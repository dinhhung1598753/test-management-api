package com.demo.app.service;

import com.demo.app.dto.chapter.ChapterRequest;
import com.demo.app.dto.chapter.ChapterResponse;
import com.demo.app.dto.subject.SubjectChaptersRequest;
import com.demo.app.dto.subject.SubjectChaptersResponse;
import com.demo.app.dto.subject.SubjectRequest;
import com.demo.app.dto.subject.SubjectResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SubjectService {
    void addSubject(SubjectRequest request) throws FieldExistedException;

    void addSubjectChapters(SubjectChaptersRequest request);

    List<SubjectResponse> getAllSubjects() throws EntityNotFoundException;

    void updateSubject(int subjectId, SubjectRequest request) throws EntityNotFoundException;

    void disableSubject(int subjectId);

    @Transactional
    List<ChapterResponse> getAllSubjectChapters(String code) throws EntityNotFoundException;

    List<SubjectChaptersResponse> getAllSubjectsWithChapters();

    SubjectChaptersResponse getSubjectWithChapter(String code);

    @Transactional
    void addSubjectChapter(String code, ChapterRequest request) throws EntityNotFoundException;

    @Transactional
    void addSubjectChapters(String code, List<ChapterRequest> request);

    void updateSubjectChapter(int chapterId, ChapterRequest request);

    void updateSubjectWithChapters(int subjectId, SubjectChaptersRequest request);

    void disableChapter(int chapterId);
}
