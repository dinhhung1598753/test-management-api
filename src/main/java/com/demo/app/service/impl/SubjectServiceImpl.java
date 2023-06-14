package com.demo.app.service.impl;

import com.demo.app.dto.chapter.ChapterRequest;
import com.demo.app.dto.chapter.ChapterResponse;
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

import java.util.HashSet;
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
    public void addSubject(SubjectRequest request) throws FieldExistedException{
        if(subjectRepository.existsByCodeAndEnabledTrue(request.getCode())){
            throw new FieldExistedException("Subject's code already taken !", HttpStatus.BAD_REQUEST);
        }
        Subject subject = mapper.map(request, Subject.class);
        subjectRepository.save(subject);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() throws EntityNotFoundException{
        List<Subject> subjects = subjectRepository.findByEnabledIsTrue();
        if (subjects.size() == 0){
            throw new EntityNotFoundException("Not found any subject !", HttpStatus.NOT_FOUND);
        }
        return subjects.stream().map((subject -> {
            var subjectResponse = mapper.map(subject, SubjectResponse.class);
            subjectResponse.setChapterQuantity(chapterRepository.countBySubjectId(subject.getId()));
            var chapterIds = subject.getChapters()
                    .stream()
                    .map(Chapter::getId)
                    .toList();
            var countQuestionByChapter = 0;
            for (var chapterId : chapterIds){
                countQuestionByChapter += questionRepository.countByChapterId(chapterId);
            }
            subjectResponse.setQuestionQuantity(countQuestionByChapter);
            return subjectResponse;
        })).collect(Collectors.toList());
    }

    @Override
    public void updateSubject(int subjectId, SubjectRequest request) throws EntityNotFoundException {
        var existSubject = subjectRepository.findById(subjectId).orElseThrow(
                () -> new EntityNotFoundException("", HttpStatus.NOT_FOUND)
        );
        String updateCode = request.getCode();
        if (!updateCode.equalsIgnoreCase(existSubject.getCode())){
            if(subjectRepository  .existsByCodeAndEnabledTrue(updateCode)){
                throw new FieldExistedException("", HttpStatus.CONFLICT);
            }
        }
        var subject = mapper.map(request, Subject.class);
        subject.setId(existSubject.getId());
        subject.setEnabled(existSubject.isEnabled());

        subjectRepository.save(subject);
    }

   @Override
   public void disableSubject(int subjectId){
       var subject = subjectRepository.findById(subjectId).orElseThrow(
               () -> new EntityNotFoundException("", HttpStatus.NOT_FOUND)
       );
       subject.setEnabled(false);
       subjectRepository.save(subject);
   }

   @Override
   @Transactional
   public List<ChapterResponse> getAllSubjectChapters(String code) throws EntityNotFoundException {
        var subject = subjectRepository.findByCode(code).orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with code %s", code), HttpStatus.NOT_FOUND));
        List<Chapter> chapters = chapterRepository.findBySubjectIdAndEnabledTrue(subject.getId());
        return chapters.stream().map(chapter -> ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .order(chapter.getOrder())
                .build()).collect(Collectors.toList());
   }

   @Override
   @Transactional
   public void addSubjectChapter(String code, ChapterRequest request) throws EntityNotFoundException{
       var subject = subjectRepository.findByCode(code)
               .orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with code %s", code), HttpStatus.NOT_FOUND));
       if (chapterRepository.existsBySubjectIdAndOrderAndEnabledTrue(subject.getId(), request.getOrder())){
           throw new FieldExistedException("This chapter already existed in subject !", HttpStatus.BAD_REQUEST);
       }
       var chapter = mapper.map(request, Chapter.class);
       chapter.setSubject(subject);
       chapterRepository.save(chapter);
   }

   @Override
   @Transactional
   public void addSubjectChapters(String code, List<ChapterRequest> request) {
        var subject = subjectRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with code %s", code), HttpStatus.NOT_FOUND));
        var chapters = new HashSet<Chapter>();
        request.forEach(chapterRequest -> {
            if (chapterRepository.existsBySubjectIdAndOrderAndEnabledTrue(subject.getId(), chapterRequest.getOrder())){
                throw new FieldExistedException("This chapter already existed in subject !", HttpStatus.BAD_REQUEST);
            }
            var chapter = mapper.map(chapterRequest, Chapter.class);
            chapter.setSubject(subject);
            chapters.add(chapter);
        });
        chapterRepository.saveAll(chapters);
   }


   @Override
   public void updateSubjectChapter(int chapterId, ChapterRequest request){
       var chapter = chapterRepository.findById(chapterId)
               .orElseThrow(() -> new EntityNotFoundException(String.format("Cannot find any chapter with id %d", chapterId), HttpStatus.NOT_FOUND));
       chapter.setTitle(request.getTitle());
       chapter.setOrder(request.getOrder());
       chapterRepository.save(chapter);
   }

   @Override
   public void disableChapter(int chapterId){
       var chapter = chapterRepository.findById(chapterId).orElseThrow(
               () -> new EntityNotFoundException(String.format("Cannot find any chapter with id %d", chapterId), HttpStatus.NOT_FOUND));
       chapter.setEnabled(false);
       chapterRepository.save(chapter);
   }

}
