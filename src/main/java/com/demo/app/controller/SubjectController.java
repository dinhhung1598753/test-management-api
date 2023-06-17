package com.demo.app.controller;

import com.demo.app.dto.chapter.ChapterRequest;
import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.subject.SubjectRequest;
import com.demo.app.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping(path = "/add")
    public ResponseEntity<?> addSubject(@RequestBody @Valid final SubjectRequest request){
        subjectService.addSubject(request);
        String message = "Add subject successfully !";
        return new ResponseEntity<>(new ResponseMessage(message), HttpStatus.CREATED);
    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> listSubject(){
        return ResponseEntity.ok().body(subjectService.getAllSubjects());
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable(name = "id") int subjectId ,@RequestBody @Valid final SubjectRequest request){
        subjectService.updateSubject(subjectId, request);
        return new ResponseEntity<>(new ResponseMessage("Update subject successfully !"), HttpStatus.OK);
    }

    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableSubject(@PathVariable(name = "id") int subjectId){
        subjectService.disableSubject(subjectId);
        return new ResponseEntity<>(new ResponseMessage("Update subject successfully !"), HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/{code}/chapter/list")
    public ResponseEntity<?> getAllSubjectChapters(@PathVariable(name = "code") String code){
        return ResponseEntity.ok().body(subjectService.getAllSubjectChapters(code));
    }

    @PostMapping(path = "/{code}/chapter/add")
    public ResponseEntity<?> addSubjectChapter(@PathVariable(name = "code") String code, @RequestBody @Valid final ChapterRequest request){
        subjectService.addSubjectChapter(code, request);
        return new ResponseEntity<>(new ResponseMessage("Add subject's chapter successfully !"), HttpStatus.CREATED);
    }

    @PutMapping(path = "/chapter/update/{id}")
    public ResponseEntity<?> updateChapter(@PathVariable(name = "id") int chapterId, @RequestBody @Valid final ChapterRequest request){
        subjectService.updateSubjectChapter(chapterId, request);
        return new ResponseEntity<>(new ResponseMessage("Update chapter successfully !"), HttpStatus.OK);
    }

    @DeleteMapping(path = "/chapter/disable/{id}")
    public ResponseEntity<?> disableChapter(@PathVariable(name = "id") int chapterId){
        subjectService.disableChapter(chapterId);
        return new ResponseEntity<>(new ResponseMessage("Disable chapter successfully !"), HttpStatus.OK);
    }
}
