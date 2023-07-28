package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.test.TestDetailRequest;
import com.demo.app.dto.test.TestQuestionRequest;
import com.demo.app.dto.test.TestRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/test")
@Tag(name = "Test", description = "Test API management")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping(path = "/create/random")
    public ResponseEntity<?> createTest(@RequestBody @Valid final TestRequest request)
            throws EntityNotFoundException {
        Integer testId = testService.createTestRandomQuestion(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testId);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<?> saveTestByChooseQuestions(@RequestBody @Valid final TestQuestionRequest request){
        var testId = testService.createTestByChooseQuestions(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(testId);
    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllTests(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testService.getAllTests());
    }

    @SuppressWarnings("DefaultLocale")
    @PutMapping(path = "/update/{id}")
    public  ResponseEntity<?> updateTest(@PathVariable(name = "id") int testId,
                                         @Valid final TestDetailRequest request){
        testService.updateTest(testId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage(String.format("Update test with id : %d successfully !", testId)));
    }

    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableTest(@PathVariable(name = "id") int testId){
        testService.disableTest(testId);
        return new ResponseEntity<>(new ResponseMessage("Disable test successfully !"), HttpStatus.OK);
    }

}
