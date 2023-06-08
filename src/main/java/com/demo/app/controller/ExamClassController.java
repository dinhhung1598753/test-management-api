package com.demo.app.controller;

import com.demo.app.dto.examClass.ClassRequest;
import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.service.ExamClassService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/class")
@Tag(name = "Exam Class")
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class ExamClassController {

    private final ExamClassService examClassService;

    @PostMapping("/create")
    public ResponseEntity<?> createExamClass(@RequestBody ClassRequest request, Principal principal){
        examClassService.createExamClass(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Create Exam Class successfully !"));
    }

    @PostMapping(path = "/add-students/{id}")
    public ResponseEntity<?> addStudentsToExamClass(@PathVariable(name = "id") int examClassId ,@RequestBody List<Integer> studentIds){
        examClassService.addStudentsToExamClass(examClassId, studentIds);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Students have been add to Exam Class !"));
    }

    @PostMapping(path = "/add-teacher/{id}")
    public ResponseEntity<?> addTeacherToExamClass(@PathVariable(name = "id") int examClassId, @RequestBody Integer teacherId){
        examClassService.addTeacherToExamClass(examClassId, teacherId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Teacher has been add to Exam Class !"));
    }

    @PostMapping(path = "/add-subject/{id}")
    public ResponseEntity<?> addSubjectToExamClass(@PathVariable(name = "id") int examClassId, @RequestBody Integer subjectId){
        examClassService.addSubjectToExamClass(examClassId, subjectId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Subject has been add to Exam Class !"));
    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllExamClass(){
        return ResponseEntity.status(HttpStatus.OK).body(examClassService.getAllEnabledExamClass());
    }

    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableExamClass(@PathVariable(name = "id") int examClassId){
        examClassService.disableExamClass(examClassId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseMessage("Disabled exam class successfully !"));
    }

}
