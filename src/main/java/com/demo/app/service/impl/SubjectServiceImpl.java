package com.demo.app.service.impl;

import com.demo.app.dto.chapter.ChapterRequest;
import com.demo.app.dto.chapter.ChapterResponse;
import com.demo.app.dto.subject.SubjectChaptersRequest;
import com.demo.app.dto.subject.SubjectChaptersResponse;
import com.demo.app.dto.subject.SubjectRequest;
import com.demo.app.dto.subject.SubjectResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.DuplicatedUniqueValueException;
import com.demo.app.exception.UserNotEnrolledException;
import com.demo.app.model.Chapter;
import com.demo.app.model.Subject;
import com.demo.app.repository.ChapterRepository;
import com.demo.app.repository.QuestionRepository;
import com.demo.app.repository.SubjectRepository;
import com.demo.app.repository.TeacherRepository;
import com.demo.app.service.SubjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final ModelMapper mapper;

    private final TeacherRepository teacherRepository;

    private final SubjectRepository subjectRepository;

    private final ChapterRepository chapterRepository;

    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public void addSubject(SubjectRequest request, Principal principal) throws DuplicatedUniqueValueException {
        if (subjectRepository.existsByCodeAndEnabledTrue(request.getCode())) {
            throw new DuplicatedUniqueValueException("Subject's code already taken !", HttpStatus.BAD_REQUEST);
        }
        var teacher = teacherRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotEnrolledException(
                        "You are not permitted to do this action !",
                        HttpStatus.FORBIDDEN));
        var subject = mapper.map(request, Subject.class);
        subject.setTeachers(Collections.singletonList(teacher));
        subjectRepository.save(subject);
    }

    @Override
    public void addSubjectChapters(SubjectChaptersRequest request, Principal principal) {
        if (subjectRepository.existsByCodeAndEnabledTrue(request.getCode())) {
            throw new DuplicatedUniqueValueException("Subject's code already taken !", HttpStatus.BAD_REQUEST);
        }
        var teacher = teacherRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotEnrolledException(
                        "You are not permitted to do this action !",
                        HttpStatus.FORBIDDEN));
        var subject = mapper.map(request, Subject.class);
        subject.getChapters().forEach(chapter -> chapter.setSubject(subject));
        subject.setTeachers(Collections.singletonList(teacher));
        subjectRepository.save(subject);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() throws EntityNotFoundException {
        List<Subject> subjects = subjectRepository.findByEnabledIsTrue();
        return subjects.stream().map(subject -> {
            var response = mapper.map(subject, SubjectResponse.class);
            var chapters = chapterRepository.findBySubjectIdAndEnabledTrue(subject.getId());
            response.setChapterQuantity(chapters.size());
            response.setQuestionQuantity(questionRepository.countByEnabledIsTrueAndChapterIn(chapters));
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateSubject(int subjectId, SubjectRequest request) throws EntityNotFoundException {
        var subject = subjectRepository.findById(subjectId).orElseThrow(
                () -> new EntityNotFoundException("Subject not found !", HttpStatus.NOT_FOUND)
        );
        if (!request.getCode().equalsIgnoreCase(subject.getCode())) {
            throw new DuplicatedUniqueValueException("Subject code already taken !", HttpStatus.CONFLICT);
        }
        subject.setId(subject.getId());
        subject.setEnabled(subject.getEnabled());
        subjectRepository.save(subject);
    }

    @Override
    public void disableSubject(int subjectId) {
        var subject = subjectRepository.findById(subjectId).orElseThrow(
                () -> new EntityNotFoundException("Subject not found !", HttpStatus.NOT_FOUND)
        );
        subject.setEnabled(false);
        subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public List<ChapterResponse> getAllSubjectChapters(String code) throws EntityNotFoundException {
        var subject = subjectRepository.findByCodeAndEnabledIsTrue(code)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with code %s", code), HttpStatus.NOT_FOUND));
        List<Chapter> chapters = chapterRepository.findBySubjectIdAndEnabledTrue(subject.getId());
        return chapters.parallelStream()
                .map(chapter -> ChapterResponse.builder()
                        .id(chapter.getId())
                        .title(chapter.getTitle())
                        .order(chapter.getOrder())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<SubjectChaptersResponse> getAllSubjectsWithChapters() {
        var subjects = subjectRepository.findByEnabledIsTrue();
        return subjects.stream()
                .map(subject -> {
                    var response = mapper.map(subject, SubjectChaptersResponse.class);
                    var chapters = chapterRepository.findBySubjectIdAndEnabledTrue(subject.getId());
                    response.setChapterQuantity(chapters.size());
                    response.setQuestionQuantity(questionRepository.countByEnabledIsTrueAndChapterIn(chapters));
                    return response;
                }).collect(Collectors.toList());
    }

    @Override
    public SubjectChaptersResponse getSubjectWithChapter(String code) {
        var subject = subjectRepository.findByCodeAndEnabledIsTrue(code)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Subject %s not found !", code),
                        HttpStatus.NOT_FOUND)
                );
        subject.getChapters()
                .removeIf(chapter -> !chapter.getEnabled());
        return mapper.map(subject, SubjectChaptersResponse.class);
    }

    @Override
    @Transactional
    public void addSubjectChapter(String code, ChapterRequest request) throws EntityNotFoundException {
        var subject = subjectRepository.findByCodeAndEnabledIsTrue(code)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cannot find any chapter with code %s", code),
                        HttpStatus.NOT_FOUND)
                );
        if (chapterRepository.existsBySubjectIdAndOrderAndEnabledTrue(subject.getId(), request.getOrder())) {
            throw new DuplicatedUniqueValueException("This chapter already existed in subject !", HttpStatus.BAD_REQUEST);
        }
        var chapter = mapper.map(request, Chapter.class);
        chapter.setSubject(subject);
        chapterRepository.save(chapter);
    }


    @Override
    @Transactional
    public void addSubjectChapters(String code, List<ChapterRequest> request) {
        var subject = subjectRepository.findByCodeAndEnabledIsTrue(code)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Cannot find any chapter with code %s", code),
                        HttpStatus.NOT_FOUND)
                );
        var chapters = request.parallelStream()
                .peek(chapterRequest -> {
                    if (chapterRepository.existsBySubjectIdAndOrderAndEnabledTrue(
                            subject.getId(),
                            chapterRequest.getOrder())) {
                        throw new DuplicatedUniqueValueException(
                                "This chapter already existed in subject !",
                                HttpStatus.BAD_REQUEST);
                    }
                }).map(chapterRequest -> {
                    var chapter = mapper.map(chapterRequest, Chapter.class);
                    chapter.setSubject(subject);
                    return chapter;
                }).collect(Collectors.toList());
        chapterRepository.saveAll(chapters);
    }


    @Override
    public void updateSubjectChapter(int chapterId, ChapterRequest request) {
        var chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Chapter not found !",
                        HttpStatus.NOT_FOUND)
                );
        chapter.setTitle(request.getTitle());
        chapter.setOrder(request.getOrder());
        chapterRepository.save(chapter);
    }


    @Override
    @Transactional
    public void updateSubjectWithChapters(String subjectCode, SubjectChaptersRequest request) {
        var subject = subjectRepository.findByCodeAndEnabledIsTrue(subjectCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject not found !",
                        HttpStatus.NOT_FOUND)
                );
        var oldChapters = chapterRepository.findBySubjectIdAndEnabledTrue(subject.getId())
                .iterator();
        var newChapters = request.getChapters()
                .stream().map(chapterRequest -> {
                    var chapter = mapper.map(chapterRequest, Chapter.class);
                    chapter.setSubject(subject);
                    chapter.setId(oldChapters.next().getId());
                    return chapter;
                }).collect(Collectors.toList());
        newChapters.forEach(System.out::println);
        subject.setTitle(request.getTitle());
        subject.setCode(request.getCode());
        subject.setCredit(request.getCredit());
        subject.setDescription(request.getDescription());
        chapterRepository.saveAll(newChapters);
        subjectRepository.save(subject);
    }

    @Override
    public void disableChapter(int chapterId) {
        var chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Chapter not found",
                        HttpStatus.NOT_FOUND)
                );
        chapter.setEnabled(false);
        chapterRepository.save(chapter);
    }

}
