package com.demo.app.controller;

import com.demo.app.dto.answer.AnswerRequest;
import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.question.QuestionRequest;
import com.demo.app.exception.FileInputException;
import com.demo.app.service.QuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/question")
@Tag(name = "Question", description = "Question APIs Management")
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping(path = "/add",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            })
    public ResponseEntity<?> addQuestion(@RequestParam("chapterId") int chapterId,
                                         @RequestPart(name = "topicText") String topicText,
                                         @RequestPart(name = "topicImageFile") MultipartFile topicImageFile,
                                         @RequestPart(name = "level") String level) throws FileInputException {
        var request = QuestionRequest.builder()
                .topicText(topicText)
                .topicImageFile(topicImageFile)
                .level(level)
                .build();
        try {
            questionService.addQuestion(chapterId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Add question successfully !"));
        } catch (IOException ex) {
            throw new FileInputException("Could not upload image !", HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping(path = "{questionId}/answers/add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addQuestionAnswers(@PathVariable int questionId, @RequestBody @Valid @NotNull final List<AnswerRequest> requests) {
        questionService.addQuestionAnswers(questionId, requests);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Add answers for question successfully !"));
    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllQuestionsBySubjectCode(@RequestParam(name = "code") String code){
        return ResponseEntity.status(HttpStatus.OK).body(questionService.getAllQuestionsBySubjectCode(code));
    }


    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable(name = "id") int questionId,
                                            @RequestPart(name = "topicText") @Valid @NotNull String topicText,
                                            @RequestPart(name = "topicImageFile") MultipartFile topicImageFile,
                                            @RequestPart(name = "level") @Valid @NotNull String level){
        var request = QuestionRequest.builder()
                .topicText(topicText)
                .topicImageFile(topicImageFile)
                .level(level)
                .build();
        questionService.updateQuestion(questionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Update question successfully !"));
    }

    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableQuestion(@PathVariable(name = "id") int questionId){
        questionService.disableQuestion(questionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
