package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.test.TestDetailRequest;
import com.demo.app.dto.test.TestQuestionRequest;
import com.demo.app.dto.test.TestRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.MyObject;
import com.demo.app.service.TestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping(path = "/api/v1/test")
@Tag(name = "Test", description = "Test API management")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping(path = "/create/random")
    public ResponseEntity<?> createTest(@RequestBody @Valid final TestRequest request) throws EntityNotFoundException {
        testService.createTestRandomQuestion(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("Test random created successfully !"));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<?> saveTestByChooseQuestions(@RequestBody @Valid final TestQuestionRequest request){
        testService.createTestByChooseQuestions(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage("Created test successfully !"));
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

    @GetMapping(path="/mark-ai")
    public ResponseEntity<?> getModelAI(@RequestParam(name="pathImg") String pathImg) throws IOException {
        class MyRunnable implements Runnable {
            public void run(){
                String CMD =
                        "cmd /c python main.py %s";
                CMD = String.format(CMD, pathImg);
                try {
                    File fileTxt = new File("result.txt");

                    if(fileTxt.exists() && !fileTxt.isDirectory()) {
                        fileTxt.delete();
                    }
                    File fileJson = new File("data.json");
                    if(fileJson.exists() && !fileJson.isDirectory()) {
                        fileJson.delete();
                    }
                    Runtime.getRuntime().exec(CMD);
                    while (true) {
                        File f = new File("result.txt");
                        if (f.exists()) return;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Thread thread = new Thread(new MyRunnable());
        thread.start();
        while (thread.isAlive());
        String filePath = "data.json";
        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        MyObject myObject = objectMapper.readValue(file, MyObject.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(myObject);
    }


}
