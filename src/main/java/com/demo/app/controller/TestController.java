package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.test.TestDetailRequest;
import com.demo.app.dto.test.TestRequest;
import com.demo.app.dto.test.TestDetailResponse;
import com.demo.app.dto.testset.TestSetRequest;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.MyObject;
import com.demo.app.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.Scanner;

@RestController
@RequestMapping(path = "/api/v1/test")
@Tag(name = "Test", description = "Test API management")
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class TestController {

    private final TestService testService;

    @PostMapping(path = "/create/first-step")
    public ResponseEntity<?> createTest(@RequestBody @Valid final TestRequest request) throws EntityNotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testService.createTestFirstStep(request));
    }

    @PostMapping(path = "/create/second-step")
    public ResponseEntity<?> saveTest(@RequestBody final TestDetailResponse response) {
        testService.createTestSecondStep(response);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage("Created test successfully !"));
    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllTests(){
        return ResponseEntity.status(HttpStatus.OK).body(testService.getAllTests());
    }

    @PostMapping(path = "/test-set/create/{id}")
    public ResponseEntity<?> createTestSetFromTest(@PathVariable(name = "id") int testId, @RequestBody @Valid final TestSetRequest request){
        testService.createTestSetFromTest(testId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage(String.format("Created Set of test with id %d successfully !", testId)));
    }




    @PutMapping(path = "/update/{id}")
    public  ResponseEntity<?> updateTest(@PathVariable(name = "id") int testId, @Valid final TestDetailRequest request){
        testService.updateTest(testId, request);
        return new ResponseEntity<>(new ResponseMessage("Update test successful !"), HttpStatus.OK);
    }

    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableTest(@PathVariable(name = "id") int testId){
        testService.disableTest(testId);
        return new ResponseEntity<>(new ResponseMessage("Disable test successfully !"), HttpStatus.OK);
    }
    @GetMapping(path="/chamdiem")
    public ResponseEntity<?> getModelAI() throws IOException, InterruptedException {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
        while (thread.isAlive());
        String filePath = "data.json";
        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        MyObject myObject = objectMapper.readValue(file, MyObject.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(myObject);
    }
    public class MyRunnable implements Runnable {
        public void run(){
            String CMD =
                    "cmd /c python app.py %s %d";
            CMD = String.format(CMD, "a7.jpg", 60);
            try {
                File fileTxt = new File("result.txt");
                if(fileTxt.exists() && !fileTxt.isDirectory()) {
                    fileTxt.delete();
                }
                File fileJson = new File("data.json");
                if(fileJson.exists() && !fileJson.isDirectory()) {
                    fileJson.delete();
                }
                Process process = Runtime.getRuntime().exec(CMD);
                while (true) {
                    File f = new File("result.txt");
                    if (f.exists()) return;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
