package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.question.MultipleQuestionRequest;
import com.demo.app.dto.question.SingleQuestionRequest;
import com.demo.app.service.QuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/question")
@Tag(name = "Question", description = "Question APIs Management")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping(path = "/add")
    public ResponseEntity<?> addQuestion(@RequestBody final SingleQuestionRequest request){
        questionService.saveQuestion(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage("Add question successfully !"));
    }

    @PostMapping(path = "/adds")
    public ResponseEntity<?> addAllQuestions(@RequestBody final MultipleQuestionRequest request){
        questionService.saveAllQuestions(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage("Add all questions successfully"));
    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllQuestionsBySubjectCode(@RequestParam(name = "code") String code) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(questionService.getAllQuestionsBySubjectCode(code));
    }


    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable(name = "id") int questionId, @RequestBody SingleQuestionRequest request) {
        questionService.updateQuestion(questionId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("Update question successfully !"));
    }

    @DeleteMapping(path = "/disable/{id}")
    @CrossOrigin(allowedHeaders = "*", origins = "*")
    public ResponseEntity<?> disableQuestion(@PathVariable(name = "id") int questionId) {
        questionService.disableQuestion(questionId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ResponseMessage("Disable question successfully !"));
    }
}
