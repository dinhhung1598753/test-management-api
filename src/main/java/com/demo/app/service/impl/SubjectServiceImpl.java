package com.demo.app.service.impl;

import com.demo.app.dto.chapter.ChapterRequest;
import com.demo.app.dto.chapter.ChapterResponse;
import com.demo.app.dto.subject.SubjectChaptersRequest;
import com.demo.app.dto.subject.SubjectChaptersResponse;
import com.demo.app.dto.subject.SubjectRequest;
import com.demo.app.dto.subject.SubjectResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FieldExistedException;
import com.demo.app.model.Chapter;
import com.demo.app.model.Subject;
import com.demo.app.repository.ChapterRepository;
import com.demo.app.repository.QuestionRepository;
import com.demo.app.repository.SubjectRepository;
import com.demo.app.service.SubjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final ModelMapper mapper;

    private final SubjectRepository subjectRepository;

    private final ChapterRepository chapterRepository;

    private final QuestionRepository questionRepository;

    @Override
    public void addSubject(SubjectRequest request) throws FieldExistedException {
        if (subjectRepository.existsByCodeAndEnabledTrue(request.getCode())) {
            throw new FieldExistedException("Subject's code already taken !", HttpStatus.BAD_REQUEST);
        }
        var subject = mapper.map(request, Subject.class);
        subjectRepository.save(subject);
    }

    @Override
    public void addSubjectChapters(SubjectChaptersRequest request) {
        if (subjectRepository.existsByCodeAndEnabledTrue(request.getCode())) {
            throw new FieldExistedException("Subject's code already taken !", HttpStatus.BAD_REQUEST);
        }
        var subject = mapper.map(request, Subject.class);
        subject.getChapters()
                .forEach(chapter -> chapter.setSubject(subject));
        subjectRepository.save(subject);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() throws EntityNotFoundException {
        List<Subject> subjects = subjectRepository.findByEnabledIsTrue();
        return subjects.stream()
                .map(subject -> {
                    var response = mapper.map(subject, SubjectResponse.class);
                    var chapters = subject.getChapters();
                    response.setChapterQuantity(chapters.size());
                    response.setQuestionQuantity(questionRepository.countByChapterIn(chapters));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateSubject(int subjectId, SubjectRequest request) throws EntityNotFoundException {
        var existSubject = subjectRepository.findById(subjectId).orElseThrow(
                () -> new EntityNotFoundException("", HttpStatus.NOT_FOUND)
        );
        String updateCode = request.getCode();
        if (!updateCode.equalsIgnoreCase(existSubject.getCode())) {
            if (subjectRepository.existsByCodeAndEnabledTrue(updateCode)) {
                throw new FieldExistedException("", HttpStatus.CONFLICT);
            }
        }
        var subject = mapper.map(request, Subject.class);
        subject.setId(existSubject.getId());
        subject.setEnabled(existSubject.getEnabled());

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
        return chapters.stream().map(chapter -> ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .order(chapter.getOrder())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<SubjectChaptersResponse> getAllSubjectsWithChapters() {
        var subjects = subjectRepository.findByEnabledIsTrue();
        return subjects.stream()
                .map(subject -> mapper.map(subject, SubjectChaptersResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public SubjectChaptersResponse getSubjectWithChapter(String code) {
        var subject = subjectRepository.findByCodeAndEnabledIsTrue(code)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Subject %s not found !", code), HttpStatus.NOT_FOUND));
        subject.getChapters().removeIf(chapter -> !chapter.getEnabled());
        return mapper.map(subject, SubjectChaptersResponse.class);
    }

    @Override
    @Transactional
    public void addSubjectChapter(String code, ChapterRequest request) throws EntityNotFoundException {
        var subject = subjectRepository.findByCodeAndEnabledIsTrue(code)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with code %s", code), HttpStatus.NOT_FOUND));
        if (chapterRepository.existsBySubjectIdAndOrderAndEnabledTrue(subject.getId(), request.getOrder())) {
            throw new FieldExistedException("This chapter already existed in subject !", HttpStatus.BAD_REQUEST);
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
                        HttpStatus.NOT_FOUND));
        var chapters = request.parallelStream()
                .peek(chapterRequest -> {
                    if (chapterRepository.existsBySubjectIdAndOrderAndEnabledTrue(
                            subject.getId(),
                            chapterRequest.getOrder())) {
                        throw new FieldExistedException(
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
        @SuppressWarnings("DefaultLocale") var chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with id %d", chapterId), HttpStatus.NOT_FOUND));
        chapter.setTitle(request.getTitle());
        chapter.setOrder(request.getOrder());
        chapterRepository.save(chapter);
    }


    @Override
    @Transactional
    public void updateSubjectWithChapters(int subjectId, SubjectChaptersRequest request) {
        var subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found !", HttpStatus.NOT_FOUND));
        var oldChapters = chapterRepository.findBySubjectIdAndEnabledTrue(subjectId).iterator();
        var newChapters = request.getChapters()
                .stream()
                .map(chapterRequest -> {
                    var chapter = mapper.map(chapterRequest, Chapter.class);
                    chapter.setSubject(subject);
                    chapter.setId(oldChapters.next().getId());
                    return chapter;
                })
                .collect(Collectors.toList());

        subject.setTitle(request.getTitle());
        subject.setCode(request.getCode());
        subject.setCredit(request.getCredit());
        subject.setDescription(request.getDescription());
        subject.setChapters(newChapters);

        subjectRepository.save(subject);
    }

    @Override
    public void disableChapter(int chapterId) {
        @SuppressWarnings("DefaultLocale") var chapter = chapterRepository.findById(chapterId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Cannot find any chapter with id %d", chapterId),
                        HttpStatus.NOT_FOUND));
        chapter.setEnabled(false);
        chapterRepository.save(chapter);
    }

}
